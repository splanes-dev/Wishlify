package com.splanes.uoc.wishlify.data.feature.secretsanta.repository

import com.splanes.uoc.wishlify.data.feature.groups.datasource.GroupsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.groups.mapper.GroupsDataMapper
import com.splanes.uoc.wishlify.data.feature.secretsanta.datasource.SecretSantaRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.secretsanta.mapper.SecretSantaDataMapper
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaAssignmentEntity
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaEventEntity
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaParticipantWishlistEntity
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.data.feature.wishlists.datasource.WishlistsRemoteDataSource
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.CreateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.UpdateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secresanta.repository.SecretSantaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SecretSantaRepositoryImpl(
  private val secretSantaRemoteDataSource: SecretSantaRemoteDataSource,
  private val userRemoteDataSource: UserRemoteDataSource,
  private val groupsRemoteDataSource: GroupsRemoteDataSource,
  private val wishlistsRemoteDataSource: WishlistsRemoteDataSource,
  private val userDataMapper: UserDataMapper,
  private val groupsMapper: GroupsDataMapper,
  private val mapper: SecretSantaDataMapper,
) : SecretSantaRepository {

  override suspend fun fetchSecretSantaEvents(uid: String): Result<List<SecretSantaEvent>> =
    runCatching {
      val groups = groupsRemoteDataSource
        .fetchGroups(uid)
        .map { groupsMapper.mapToBasic(it, true) }
      val groupsId = groups.map { it.id }

      val entities = secretSantaRemoteDataSource.fetchSecretSantaEvents(uid, groupsId)

      val assignments = coroutineScope {
        entities
          .filter { event -> event.drawStatus == SecretSantaEventEntity.DrawStatus.Done }
          .map { event -> event.id }
          .map { id ->
            async {
              val assignment = secretSantaRemoteDataSource.fetchAssignment(uid, id)
              assignment?.let { id to assignment.target }
            }
          }
          .awaitAll()
          .filterNotNull()
          .map { (id, uid) ->
            async {
              val user = userRemoteDataSource.fetchUserById(uid)
              user
                ?.let(userDataMapper::mapToBasic)
                ?.let(userDataMapper::map)
                ?.let { u -> id to u }
            }
          }
          .awaitAll()
          .filterNotNull()
          .toMap()
      }

      entities.map { entity -> mapper.map(entity, assignments) }
    }

  override suspend fun fetchSecretSantaEvent(
    uid: String,
    eventId: String
  ): Result<SecretSantaEventDetail> =
    runCatching {
      coroutineScope {
        val entity = secretSantaRemoteDataSource.fetchSecretSantaEvent(eventId)
          ?: error("No secret-santa event found for ID: $eventId")

        val group = entity.group?.let { id -> groupsRemoteDataSource.fetchGroupById(id) }

        var receiverHobbiesEnabled = false
        val receiver = if (entity.drawStatus == SecretSantaEventEntity.DrawStatus.Done) {
          val assignmentEntity = secretSantaRemoteDataSource.fetchAssignment(uid, eventId)
          assignmentEntity?.target
        } else {
          null
        }

        val usersToFetch = buildList {
          add(entity.createdBy)
          addAll(entity.participants)
          receiver?.let(::add)
          group?.members?.run(::addAll)
        }.distinct()

        val usersDeferred = async {
          usersToFetch
            .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
            .awaitAll()
            .filterNotNull()
            .also { entities ->
              receiverHobbiesEnabled =
                entities.find { it.uid == receiver }?.hobbies?.enabled == true
            }
            .map { entity -> userDataMapper.mapToBasic(entity) }
            .map { basic -> userDataMapper.map(basic) }
            .associateBy { user -> user.uid }
        }

        val receiverWishlistDeferred = async {
          if (entity.drawStatus == SecretSantaEventEntity.DrawStatus.Done && receiver != null) {
            val participantWishlistEntity =
              secretSantaRemoteDataSource.fetchParticipantWishlist(eventId, receiver)
            participantWishlistEntity?.wishlist
          } else {
            null
          }
        }

        val currentUserWishlistDeferred = async {
          if (entity.drawStatus == SecretSantaEventEntity.DrawStatus.Done) {
            val participantWishlistEntity =
              secretSantaRemoteDataSource.fetchParticipantWishlist(eventId, uid)
            participantWishlistEntity?.wishlist
          } else {
            null
          }
        }

        val receiverWishlist = receiverWishlistDeferred.await()
        val currentUserWishlist = currentUserWishlistDeferred.await()
        val users = usersDeferred.await()

        mapper.mapDetail(
          entity = entity,
          group = group?.let { g -> groupsMapper.mapToBasic(g, isActive = true) },
          receiver = receiver,
          receiverWishlist = receiverWishlist,
          receiverSharedHobbies = receiverHobbiesEnabled,
          currentUserWishlist = currentUserWishlist,
          users = users
        )
      }
    }

  override suspend fun createSecretSantaEvent(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateSecretSantaEventRequest
  ): Result<Unit> =
    runCatching {
      val entity = mapper.map(uid, imageMedia, request)
      secretSantaRemoteDataSource.upsertSecretSantaEvent(entity)
    }

  override suspend fun updateSecretSantaEvent(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateSecretSantaEventRequest
  ): Result<Unit> =
    runCatching {
      val entity = mapper.map(uid, imageMedia, request)
      secretSantaRemoteDataSource.upsertSecretSantaEvent(entity)
    }

  override suspend fun doSecretSantaDraw(
    uid: String,
    eventId: String,
    assignments: Map<String, String>
  ): Result<Unit> =
    runCatching {
      coroutineScope {
        val event = secretSantaRemoteDataSource.fetchSecretSantaEvent(eventId)
          ?: error("No secret santa event found for id=$eventId")

        val entities = assignments.mapValues { (_, target) -> SecretSantaAssignmentEntity(target) }
        entities
          .map { (uid, assignment) ->
            async { secretSantaRemoteDataSource.upsertAssignment(eventId, uid, assignment) }
          }
          .awaitAll()

        val eventUpdated = event.copy(drawStatus = SecretSantaEventEntity.DrawStatus.Done)
        secretSantaRemoteDataSource.upsertSecretSantaEvent(entity = eventUpdated)
      }
    }

  override suspend fun shareWishlistToGiver(
    uid: String,
    eventId: String,
    wishlistId: String
  ): Result<Unit> =
    runCatching {
      coroutineScope {
        val items = wishlistsRemoteDataSource.fetchWishlistItems(wishlistId)
        val filtered = items.filter { it.purchased == null }

        secretSantaRemoteDataSource.upsertParticipantWishlist(
          eventId = eventId,
          uid = uid,
          entity = SecretSantaParticipantWishlistEntity(wishlistId)
        )

        filtered
          .map { entity ->
            async {
              secretSantaRemoteDataSource.upsertParticipantWishlistItem(
                eventId = eventId,
                uid = uid,
                entity = entity
              )
            }
          }
          .awaitAll()
      }
    }
}
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
import com.splanes.uoc.wishlify.data.feature.wishlists.mapper.WishlistsDataMapper
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.ChatPage
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.CreateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaChatMessage
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaWishlist
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.UpdateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SecretSantaRepositoryImpl(
  private val secretSantaRemoteDataSource: SecretSantaRemoteDataSource,
  private val userRemoteDataSource: UserRemoteDataSource,
  private val groupsRemoteDataSource: GroupsRemoteDataSource,
  private val wishlistsRemoteDataSource: WishlistsRemoteDataSource,
  private val userDataMapper: UserDataMapper,
  private val groupsMapper: GroupsDataMapper,
  private val wishlistMapper: WishlistsDataMapper,
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
              assignment?.let { id to assignment.receiver }
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
        val assignment = if (entity.drawStatus == SecretSantaEventEntity.DrawStatus.Done) {
          secretSantaRemoteDataSource.fetchAssignment(uid, eventId)
        } else {
          null
        }

        val usersToFetch = buildList {
          add(entity.createdBy)
          addAll(entity.participants)
          assignment?.receiver?.let(::add)
          group?.members?.run(::addAll)
        }.distinct()

        val usersDeferred = async {
          usersToFetch
            .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
            .awaitAll()
            .filterNotNull()
            .also { entities ->
              receiverHobbiesEnabled =
                entities.find { it.uid == assignment?.receiver }?.hobbies?.enabled == true
            }
            .map { entity -> userDataMapper.mapToBasic(entity) }
            .map { basic -> userDataMapper.map(basic) }
            .associateBy { user -> user.uid }
        }

        val receiverWishlistDeferred = async {
          if (entity.drawStatus == SecretSantaEventEntity.DrawStatus.Done && assignment?.receiver != null) {
            val participantWishlistEntity =
              secretSantaRemoteDataSource.fetchParticipantWishlist(eventId, assignment.receiver)
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
          receiver = assignment?.receiver,
          giver = assignment?.giver,
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

        val entities = assignments.mapValues { ( user, receiver) ->
          val giver = assignments.keys.first { u -> assignments[u] == user }
          SecretSantaAssignmentEntity(receiver, giver)
        }
        entities
          .map { (uid, assignment) ->
            async { secretSantaRemoteDataSource.upsertAssignment(eventId, uid, assignment) }
          }
          .awaitAll()

        val eventUpdated = event.copy(drawStatus = SecretSantaEventEntity.DrawStatus.Done)
        secretSantaRemoteDataSource.upsertSecretSantaEvent(entity = eventUpdated)

        val chats = mapper.createChatsFromAssignments(assignments)

        chats
          .map { entity ->
            async { secretSantaRemoteDataSource.upsertSecretSantaEventChat(eventId, entity) }
          }
          .awaitAll()
      }
    }

  override suspend fun shareWishlistToGiver(
    uid: String,
    eventId: String,
    wishlistId: String
  ): Result<Unit> =
    runCatching {
      coroutineScope {
        val wishlist = wishlistsRemoteDataSource.fetchWishlist(wishlistId)
        val items = wishlistsRemoteDataSource.fetchWishlistItems(wishlistId)
        val filtered = items.filter { it.purchased == null }

        secretSantaRemoteDataSource.upsertParticipantWishlist(
          eventId = eventId,
          uid = uid,
          entity = SecretSantaParticipantWishlistEntity(wishlistId, wishlist.title)
        )

        secretSantaRemoteDataSource.removeParticipantWishlistItems(
          eventId = eventId,
          uid = uid
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

  override suspend fun unshareWishlistToGiver(
    uid: String,
    eventId: String
  ): Result<Unit> =
    runCatching {
      secretSantaRemoteDataSource.removeParticipantWishlistItems(
        eventId = eventId,
        uid = uid
      )

      secretSantaRemoteDataSource.removeParticipantWishlist(
        eventId = eventId,
        uid = uid
      )
    }

  override suspend fun fetchSecretSantaWishlist(
    eventId: String,
    wishlistOwnerId: String
  ): Result<SecretSantaWishlist> =
    runCatching {
      val entity = secretSantaRemoteDataSource.fetchParticipantWishlist(eventId, wishlistOwnerId)
        ?: error("No participant wishlist found for event $eventId and owner $wishlistOwnerId")
      SecretSantaWishlist(entity.wishlist, entity.title)
    }

  override suspend fun fetchSecretSantaWishlistItems(
    eventId: String,
    wishlistOwnerId: String
  ): Result<List<WishlistItem>> =
    runCatching {
      coroutineScope {
        val entities = secretSantaRemoteDataSource.fetchParticipantWishlistItems(
          eventId = eventId,
          uid = wishlistOwnerId
        )

        val users = entities
          .map { async { userRemoteDataSource.fetchUserById(it.createdBy) } }
          .awaitAll()
          .filterNotNull()
          .map { user -> userDataMapper.mapToBasic(user) }

        entities.map { entity -> wishlistMapper.mapItem(entity, users) }
      }
    }

  override suspend fun subscribeToSecretSantaChatMessages(
    uid: String,
    eventId: String,
    chatId: String,
    limit: Int
  ): Result<Flow<List<SecretSantaChatMessage>>> =
    runCatching {
      coroutineScope {
        val assignment = secretSantaRemoteDataSource.fetchAssignment(uid, eventId)
          ?: error("No assignment found for eventId=$eventId and user=$uid")

        val usersToFetch = listOf(assignment.giver, assignment.receiver)
        val users = usersToFetch
          .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
          .awaitAll()
          .filterNotNull()
          .map { user -> userDataMapper.mapToBasic(user) }
          .map { basic -> userDataMapper.map(basic) }

        secretSantaRemoteDataSource
          .subscribeToChat(eventId, chatId, limit)
          .map { entities ->
            entities.map { message ->
              mapper.mapMessage(
                entity = message,
                chatId = chatId,
                uid = uid,
                users = users
              )
            }
          }
      }
    }

  override suspend fun fetchSecretSantaChatMessages(
    uid: String,
    eventId: String,
    chatId: String,
    cursor: Long,
    limit: Int
  ): Result<ChatPage<SecretSantaChatMessage>> =
    runCatching {
      coroutineScope {
        val assignment = secretSantaRemoteDataSource.fetchAssignment(uid, eventId)
          ?: error("No assignment found for eventId=$eventId and user=$uid")

        val usersToFetch = listOf(assignment.giver, assignment.receiver)

        val pageDeferred = async {
          secretSantaRemoteDataSource.fetchSecretSantaEventChatMessages(
            eventId = eventId,
            chatId = chatId,
            from = cursor,
            limit = limit,
          )
        }

        val usersDeferred = async {
          usersToFetch
            .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
            .awaitAll()
            .filterNotNull()
            .map { user -> userDataMapper.mapToBasic(user) }
            .map { basic -> userDataMapper.map(basic) }
        }

        val page = pageDeferred.await()
        val users = usersDeferred.await()

        val messages = page.messages.map { message ->
          mapper.mapMessage(
            entity = message,
            chatId = chatId,
            uid = uid,
            users = users
          )
        }

        ChatPage(
          messages = messages,
          nextCursor = page.nextCursor,
          hasMore = page.hasMore
        )
      }
    }

  override suspend fun sendMessageToChat(
    uid: String,
    eventId: String,
    chatId: String,
    text: String
  ): Result<Unit> =
    runCatching {
      val entity = mapper.mapMessage(uid = uid, text = text)
      secretSantaRemoteDataSource.upsertSecretSantaEventChatMessage(
        eventId = eventId,
        chatId = chatId,
        entity = entity
      )
    }

  override suspend fun addEventParticipantByToken(token: String): Result<Unit> =
    runCatching {
      secretSantaRemoteDataSource.addEventParticipantByToken(token)
    }
}
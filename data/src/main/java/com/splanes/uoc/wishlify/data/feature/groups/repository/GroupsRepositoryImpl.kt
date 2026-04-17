package com.splanes.uoc.wishlify.data.feature.groups.repository

import com.splanes.uoc.wishlify.data.feature.groups.datasource.GroupsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.groups.mapper.GroupsDataMapper
import com.splanes.uoc.wishlify.data.feature.secretsanta.datasource.SecretSantaRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.shared.datasource.SharedWishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.groups.model.CreateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.model.UpdateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GroupsRepositoryImpl(
  private val groupsRemoteDataSource: GroupsRemoteDataSource,
  private val sharedWishlistsRemoteDataSource: SharedWishlistsRemoteDataSource,
  private val secretSantaRemoteDataSource: SecretSantaRemoteDataSource,
  private val userRemoteDataSource: UserRemoteDataSource,
  private val userMapper: UserDataMapper,
  private val mapper: GroupsDataMapper,
) : GroupsRepository {

  override suspend fun fetchGroups(uid: String): Result<List<Group.Basic>> =
    runCatching {
      val entities = groupsRemoteDataSource.fetchGroups(uid)

      val stateMap = coroutineScope {
        async {
          entities.map { entity ->
            async {
              val wishlistDeferred = async {
                sharedWishlistsRemoteDataSource.countSharedWishlistsByGroup(entity.id)
              }
              val eventsDeferred = async {
                secretSantaRemoteDataSource.countSecretSantaEventsByGroup(entity.id)
              }
              val wishlistCount = wishlistDeferred.await()
              val eventsCount = eventsDeferred.await()
              entity.id to (wishlistCount + eventsCount)
            }
          }.awaitAll().associate { (id, count) -> id to (count > 0) }
        }.await()
      }

      entities.map { entity ->
        mapper.mapToBasic(entity, isActive = stateMap[entity.id] ?: false)
      }
    }

  override suspend fun addGroup(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateGroupRequest
  ): Result<Unit> =
    runCatching {
      val entity = mapper.mapFromRequest(uid, imageMedia, request)
      groupsRemoteDataSource.upsertGroup(entity)
    }

  override suspend fun fetchGroup(
    uid: String,
    groupId: String
  ): Result<Group.Detail> =
    runCatching {
      coroutineScope {
        val entity = groupsRemoteDataSource.fetchGroupById(groupId)
          ?: error("No group found for id $groupId")

        val usersDeferred = async {
          entity.members
            .map { id ->
              async { userRemoteDataSource.fetchUserById(id) }
            }
            .awaitAll()
            .filterNotNull()
            .map { user -> userMapper.mapToBasic(user) }
            .map { basic -> userMapper.map(basic) }
            .associateBy { u -> u.uid }
        }

        val wishlistDeferred = async {
          sharedWishlistsRemoteDataSource.countSharedWishlistsByGroup(entity.id)
        }
        val eventsDeferred = async {
          secretSantaRemoteDataSource.countSecretSantaEventsByGroup(entity.id)
        }

        val users = usersDeferred.await()
        val wishlistsCount = wishlistDeferred.await()
        val eventsCount = eventsDeferred.await()

        mapper.mapToDetail(
          uid = uid,
          entity = entity,
          users = users,
          hasSharedWishlists = wishlistsCount > 0,
          hasSecretSantaEvents = eventsCount > 0,
        )
      }
    }

  override suspend fun updateGroup(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateGroupRequest
  ): Result<Unit> =
    runCatching {
      val entity = mapper.mapFromRequest(uid, imageMedia, request)
      groupsRemoteDataSource.upsertGroup(entity)
    }

  override suspend fun deleteGroup(
    groupId: String
  ): Result<Unit> =
    runCatching {
      groupsRemoteDataSource.deleteGroup(groupId)
    }
}
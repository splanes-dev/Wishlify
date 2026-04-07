package com.splanes.uoc.wishlify.data.feature.groups.repository

import com.splanes.uoc.wishlify.data.feature.groups.datasource.GroupsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.groups.mapper.GroupsDataMapper
import com.splanes.uoc.wishlify.data.feature.shared.datasource.SharedWishlistsRemoteDataSource
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.groups.model.CreateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GroupsRepositoryImpl(
  private val groupsRemoteDataSource: GroupsRemoteDataSource,
  private val sharedWishlistsRemoteDataSource: SharedWishlistsRemoteDataSource,
  private val mapper: GroupsDataMapper,
) : GroupsRepository {

  override suspend fun fetchGroups(uid: String): Result<List<Group.Basic>> =
    runCatching {
      val entities = groupsRemoteDataSource.fetchGroups(uid)

      val stateMap = coroutineScope {
        async {
          entities.map { entity ->
            async {
              entity.id to sharedWishlistsRemoteDataSource.countSharedWishlistsByGroup(entity.id)
              // TODO: Do the same than above for secret-santa
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
      groupsRemoteDataSource.addGroup(entity)
    }
}
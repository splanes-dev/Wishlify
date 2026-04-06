package com.splanes.uoc.wishlify.data.feature.groups.repository

import com.splanes.uoc.wishlify.data.feature.groups.datasource.GroupsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.groups.mapper.GroupsDataMapper
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository

class GroupsRepositoryImpl(
  private val groupsRemoteDataSource: GroupsRemoteDataSource,
  private val groupsDataMapper: GroupsDataMapper,
) : GroupsRepository {

  override suspend fun fetchGroups(uid: String): Result<List<Group.Basic>> =
    runCatching {
      val entities = groupsRemoteDataSource.fetchGroups(uid)
      entities.map(groupsDataMapper::mapToBasic)
    }
}
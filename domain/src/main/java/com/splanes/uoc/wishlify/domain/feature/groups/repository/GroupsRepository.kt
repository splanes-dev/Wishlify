package com.splanes.uoc.wishlify.domain.feature.groups.repository

import com.splanes.uoc.wishlify.domain.feature.groups.model.Group

interface GroupsRepository {

  suspend fun fetchGroups(uid: String): Result<List<Group.Basic>>
}
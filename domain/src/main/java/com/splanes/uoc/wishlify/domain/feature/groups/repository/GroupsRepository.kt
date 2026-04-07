package com.splanes.uoc.wishlify.domain.feature.groups.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.groups.model.CreateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group

interface GroupsRepository {

  suspend fun fetchGroups(uid: String): Result<List<Group.Basic>>
  suspend fun addGroup(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateGroupRequest
  ): Result<Unit>
}
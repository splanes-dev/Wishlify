package com.splanes.uoc.wishlify.domain.feature.groups.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.groups.model.CreateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.model.UpdateGroupRequest

interface GroupsRepository {

  suspend fun fetchGroups(uid: String): Result<List<Group.Basic>>
  suspend fun addGroup(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateGroupRequest
  ): Result<Unit>

  suspend fun fetchGroup(uid: String, groupId: String): Result<Group.Detail>

  suspend fun updateGroup(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateGroupRequest
  ): Result<Unit>

  suspend fun deleteGroup(groupId: String): Result<Unit>
}
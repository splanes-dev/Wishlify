package com.splanes.uoc.wishlify.domain.feature.groups.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.groups.model.CreateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.model.UpdateGroupRequest

/**
 * Repository contract for managing groups and their persisted state.
 */
interface GroupsRepository {

  /** Retrieves the groups visible to the given user. */
  suspend fun fetchGroups(uid: String): Result<List<Group.Basic>>

  /** Creates a new group owned or managed by the given user. */
  suspend fun addGroup(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateGroupRequest
  ): Result<Unit>

  /** Retrieves the detailed view of a single group for the given user. */
  suspend fun fetchGroup(uid: String, groupId: String): Result<Group.Detail>

  /** Persists the updated state of an existing group. */
  suspend fun updateGroup(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateGroupRequest
  ): Result<Unit>

  /** Deletes the group identified by [groupId]. */
  suspend fun deleteGroup(groupId: String): Result<Unit>
}

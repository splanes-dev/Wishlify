package com.splanes.uoc.wishlify.data.feature.groups.mapper

import com.splanes.uoc.wishlify.data.common.utils.nowInMillis
import com.splanes.uoc.wishlify.data.feature.groups.model.GroupEntity
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.groups.model.CreateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.model.UpdateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User

/** Maps group entities between Firestore persistence and domain projections. */
class GroupsDataMapper {

  /** Maps a persisted group entity into the lightweight domain group projection. */
  fun mapToBasic(entity: GroupEntity, isActive: Boolean): Group.Basic =
    Group.Basic(
      id = entity.id,
      name = entity.name,
      photoUrl = entity.photoUrl,
      members = entity.members,
      state = if (isActive) Group.State.Active else Group.State.Inactive
    )

  /** Maps a group creation request into the Firestore persistence model. */
  fun mapFromRequest(uid: String, imageMedia: ImageMedia?, request: CreateGroupRequest): GroupEntity =
    GroupEntity(
      id = request.id,
      name = request.name,
      photoUrl = when (imageMedia) {
        is ImageMedia.Url -> imageMedia.url
        else -> null
      },
      members = request.members + uid,
      createdBy = uid,
      createdAt = nowInMillis()
    )

  /** Maps a group update request into the Firestore persistence model. */
  fun mapFromRequest(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateGroupRequest,
  ): GroupEntity =
    GroupEntity(
      id = request.id,
      name = request.name,
      photoUrl = when (imageMedia) {
        is ImageMedia.Url -> imageMedia.url
        else -> null
      },
      members = if (request.includeCurrentUser) {
        request.members + uid
      } else {
        request.members - uid
      }.distinct(),
      createdBy = uid,
      createdAt = nowInMillis()
    )

  /**
   * Maps a persisted group entity into the detailed domain projection using
   * resolved member profiles and external activity flags.
   */
  fun mapToDetail(
    uid: String,
    entity: GroupEntity,
    users: Map<String, User.Basic>,
    hasSharedWishlists: Boolean,
    hasSecretSantaEvents: Boolean
  ): Group.Detail =
    Group.Detail(
      id = entity.id,
      name = entity.name,
      photoUrl = entity.photoUrl,
      members = entity.members.mapNotNull { m -> users[m] },
      currentUserUid = uid,
      hasSharedWishlists = hasSharedWishlists,
      hasSecretSantaEvents = hasSecretSantaEvents
    )
}

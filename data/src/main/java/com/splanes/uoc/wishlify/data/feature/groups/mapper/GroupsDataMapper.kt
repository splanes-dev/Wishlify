package com.splanes.uoc.wishlify.data.feature.groups.mapper

import com.splanes.uoc.wishlify.data.common.utils.nowInMillis
import com.splanes.uoc.wishlify.data.feature.groups.model.GroupEntity
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.groups.model.CreateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group

class GroupsDataMapper {

  fun mapToBasic(entity: GroupEntity, isActive: Boolean): Group.Basic =
    Group.Basic(
      id = entity.id,
      name = entity.name,
      photoUrl = entity.photoUrl,
      members = entity.members,
      state = if (isActive) Group.State.Active else Group.State.Inactive
    )

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
}
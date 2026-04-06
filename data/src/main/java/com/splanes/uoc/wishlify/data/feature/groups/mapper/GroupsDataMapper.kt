package com.splanes.uoc.wishlify.data.feature.groups.mapper

import com.splanes.uoc.wishlify.data.feature.groups.model.GroupEntity
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group

class GroupsDataMapper {

  fun mapToBasic(entity: GroupEntity): Group.Basic =
    Group.Basic(
      id = entity.id,
      name = entity.name,
      photoUrl = entity.photoUrl,
      members = entity.members
    )
}
package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.mapper

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.utils.newUuid
import com.splanes.uoc.wishlify.domain.feature.groups.model.CreateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.model.UpdateGroupRequest
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupForm

class GroupsNewGroupFormMapper {

  fun requestOf(form: GroupsNewGroupForm): CreateGroupRequest =
    CreateGroupRequest(
      id = newUuid(),
      image = when (val res = form.photo) {
        is ImagePicker.Device -> ImageMediaRequest.Device(uri = res.uri.toString())
        is ImagePicker.Url -> ImageMediaRequest.Url(url = res.url)
        else -> null
      },
      name = form.name,
      members = form.members.map { it.uid }
    )

  fun requestOf(
    id: String,
    form: GroupsNewGroupForm,
    includeCurrentUser: Boolean
  ): UpdateGroupRequest =
    UpdateGroupRequest(
      id = id,
      name = form.name,
      members = form.members.map { it.uid },
      image = when (val res = form.photo) {
        is ImagePicker.Device -> ImageMediaRequest.Device(uri = res.uri.toString())
        is ImagePicker.Url -> ImageMediaRequest.Url(url = res.url)
        else -> null
      },
      includeCurrentUser = includeCurrentUser
    )

  fun autofill(group: Group.Detail): GroupsNewGroupForm =
    GroupsNewGroupForm(
      photo = group.photoUrl?.let(ImagePicker::Url),
      name = group.name,
      members = group.members.filter { it.uid != group.currentUserUid }
    )
}
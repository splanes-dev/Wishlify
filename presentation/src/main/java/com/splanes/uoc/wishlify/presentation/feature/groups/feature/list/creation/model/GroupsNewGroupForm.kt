package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker

data class GroupsNewGroupForm(
  val photo: ImagePicker.Resource? = null,
  val name: String = "",
  val members: List<User.Basic> = emptyList()
) {
  enum class Input {
    Name,
    Members
  }
}

data class GroupsNewGroupUiFormErrors(
  val nameError: String?,
  val membersError: String?,
)

data class GroupsNewGroupFormErrors(
  val nameError: NameNewGroupFormError? = null,
  val membersError: MembersNewGroupFormError? = null
)
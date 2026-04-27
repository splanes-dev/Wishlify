package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker

/** Reusable form state shared by the group creation and edition flows. */
data class GroupsNewGroupForm(
  val photo: ImagePicker.Resource? = null,
  val name: String = "",
  val members: List<User.Basic> = emptyList()
) {
  /** Identifies the editable fields of the group form. */
  enum class Input {
    Name,
    Members
  }
}

/** UI-ready validation messages rendered by the group form screens. */
data class GroupsNewGroupUiFormErrors(
  val nameError: String?,
  val membersError: String?,
)

/** Internal validation errors produced before mapping them into UI strings. */
data class GroupsNewGroupFormErrors(
  val nameError: NameNewGroupFormError? = null,
  val membersError: MembersNewGroupFormError? = null
)

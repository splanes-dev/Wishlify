package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation

import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupForm
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupUiFormErrors

/** UI state rendered by the group creation flow. */
data class GroupsNewGroupUiState(
  val form: GroupsNewGroupForm,
  val formErrors: GroupsNewGroupUiFormErrors,
  val isLoading: Boolean,
  val error: ErrorUiModel?,
)

/** One-off effects emitted by the group creation flow. */
sealed interface GroupsNewGroupUiSideEffect {
  /** Requests the parent flow to refresh after a group has been created. */
  data object GroupCreated : GroupsNewGroupUiSideEffect
}

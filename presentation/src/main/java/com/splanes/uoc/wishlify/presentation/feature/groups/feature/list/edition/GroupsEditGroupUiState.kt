package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.edition

import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupForm
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupUiFormErrors

/** UI states rendered by the group edition flow. */
sealed interface GroupsEditGroupUiState {

  /** Fullscreen loading state used while the group to edit is being resolved. */
  data class Loading(val groupName: String) : GroupsEditGroupUiState
  /** Fullscreen error state used when the requested group cannot be resolved. */
  data class Error(val groupName: String) : GroupsEditGroupUiState
  /** Form state used to edit the selected group. */
  data class Form(
    val group: Group,
    val form: GroupsNewGroupForm,
    val formErrors: GroupsNewGroupUiFormErrors,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ) : GroupsEditGroupUiState
}

/** One-off effects emitted by the group edition flow. */
sealed interface GroupsEditGroupUiSideEffect {
  /** Requests the parent flow to refresh after a group has been updated. */
  data object GroupUpdated : GroupsEditGroupUiSideEffect
}

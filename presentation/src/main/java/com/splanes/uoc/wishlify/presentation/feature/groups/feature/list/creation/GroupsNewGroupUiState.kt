package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation

import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupForm
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupUiFormErrors

data class GroupsNewGroupUiState(
  val form: GroupsNewGroupForm,
  val formErrors: GroupsNewGroupUiFormErrors,
  val isLoading: Boolean,
  val error: ErrorUiModel?,
)

sealed interface GroupsNewGroupUiSideEffect {
  data object GroupCreated : GroupsNewGroupUiSideEffect
}
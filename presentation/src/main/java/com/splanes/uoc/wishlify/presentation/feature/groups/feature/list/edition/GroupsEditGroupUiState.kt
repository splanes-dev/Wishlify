package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.edition

import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupForm
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupUiFormErrors

sealed interface GroupsEditGroupUiState {

  data class Loading(val groupName: String) : GroupsEditGroupUiState
  data class Error(val groupName: String) : GroupsEditGroupUiState
  data class Form(
    val group: Group,
    val form: GroupsNewGroupForm,
    val formErrors: GroupsNewGroupUiFormErrors,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ) : GroupsEditGroupUiState
}

sealed interface GroupsEditGroupUiSideEffect {
  data object GroupUpdated : GroupsEditGroupUiSideEffect
}
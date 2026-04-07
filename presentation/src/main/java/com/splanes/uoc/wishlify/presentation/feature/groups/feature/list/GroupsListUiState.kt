package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list

import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

sealed interface GroupsListUiState {

  data object Loading : GroupsListUiState

  data class Empty(
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : GroupsListUiState

  data class Groups(
    val groups: List<Group.Basic>,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : GroupsListUiState
}
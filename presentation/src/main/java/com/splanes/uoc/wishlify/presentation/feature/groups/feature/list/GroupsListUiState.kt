package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list

import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/** UI states rendered by the groups list flow. */
sealed interface GroupsListUiState {

  /** Fullscreen loading state used while the groups list is initially fetched. */
  data object Loading : GroupsListUiState

  /** State shown when no groups are available for the current user. */
  data class Empty(
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : GroupsListUiState

  /** State shown when the current user has one or more groups available. */
  data class Groups(
    val groups: List<Group.Basic>,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : GroupsListUiState
}

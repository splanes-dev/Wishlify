package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail

import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

sealed interface GroupDetailUiState {

  data class Loading(val groupName: String) : GroupDetailUiState
  data class Error(val groupName: String) : GroupDetailUiState
  data class Detail(
    val group: Group.Detail,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ): GroupDetailUiState
}

sealed interface GroupDetailUiSideEffect {
  data object GroupUpdated : GroupDetailUiSideEffect
}
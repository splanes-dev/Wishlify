package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list

import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

sealed interface SecretSantaListUiState {

  data object Loading : SecretSantaListUiState

  data class Empty(
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : SecretSantaListUiState

  data class Events(
    val events: List<SecretSantaEvent>,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ) : SecretSantaListUiState
}
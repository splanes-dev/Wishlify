package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list

import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/**
 * UI state for the Secret Santa events list screen.
 */
sealed interface SecretSantaListUiState {

  data object Loading : SecretSantaListUiState

  data class Empty(
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : SecretSantaListUiState

  data class Events(
    val events: List<SecretSantaEvent>,
    val isPermissionModalVisible: Boolean,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ) : SecretSantaListUiState
}

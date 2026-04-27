package com.splanes.uoc.wishlify.presentation.feature.profile.feature.main

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/**
 * UI state for the main profile screen.
 */
sealed interface ProfileMainUiState {
  data object Loading : ProfileMainUiState
  data object Error : ProfileMainUiState
  data class Profile(
    val user: User.BasicProfile,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : ProfileMainUiState
}

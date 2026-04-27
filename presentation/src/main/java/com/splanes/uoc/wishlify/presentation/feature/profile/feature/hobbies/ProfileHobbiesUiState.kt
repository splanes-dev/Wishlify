package com.splanes.uoc.wishlify.presentation.feature.profile.feature.hobbies

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/**
 * UI state for the hobbies administration flow.
 */
sealed interface ProfileHobbiesUiState {
  data object Loading: ProfileHobbiesUiState
  data object Error: ProfileHobbiesUiState
  data class Hobbies(
    val user: User.HobbiesProfile,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ): ProfileHobbiesUiState
}

/**
 * One-off effects emitted by the hobbies administration flow.
 */
sealed interface ProfileHobbiesUiSideEffect {
  data object HobbiesUpdated : ProfileHobbiesUiSideEffect
}

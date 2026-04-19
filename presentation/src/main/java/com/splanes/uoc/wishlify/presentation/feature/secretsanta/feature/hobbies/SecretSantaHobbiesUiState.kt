package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

sealed interface SecretSantaHobbiesUiState {
  data object Loading : SecretSantaHobbiesUiState
  data object Error : SecretSantaHobbiesUiState
  data class Hobbies(
    val user: User.HobbiesProfile,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : SecretSantaHobbiesUiState
}
package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.model.GiftSuggestionUiModel

/**
 * UI state for the receiver hobbies sheet shown from a Secret Santa event.
 */
sealed interface SecretSantaHobbiesUiState {
  data object Loading : SecretSantaHobbiesUiState
  data object Error : SecretSantaHobbiesUiState
  data class Hobbies(
    val user: User.HobbiesProfile,
    val suggestions: List<GiftSuggestionUiModel>,
    val isSuggestionsModalOpen: Boolean,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : SecretSantaHobbiesUiState
}

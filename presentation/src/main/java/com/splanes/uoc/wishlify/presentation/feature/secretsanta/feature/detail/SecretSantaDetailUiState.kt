package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail

import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/**
 * UI state for the Secret Santa event detail screen.
 */
sealed interface SecretSantaDetailUiState {

  data class Error(
    val eventName: String
  ) : SecretSantaDetailUiState

  data class Loading(
    val eventName: String
  ) : SecretSantaDetailUiState

  data class Detail(
    val eventName: String,
    val event: SecretSantaEventDetail,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : SecretSantaDetailUiState
}

/**
 * One-off navigation effects emitted from the Secret Santa event detail screen.
 */
sealed interface SecretSantaDetailUiSideEffect {
  data class NavToEdit(val event: String) : SecretSantaDetailUiSideEffect
  data class NavToShareWishlist(val event: String) : SecretSantaDetailUiSideEffect
  data class NavToWishlist(
    val eventId: String,
    val wishlistOwnerId: String?,
    val isOwnWishlist: Boolean,
  ) : SecretSantaDetailUiSideEffect

  data class NavToChat(
    val eventId: String,
    val chatType: String,
    val otherUid: String,
  ) : SecretSantaDetailUiSideEffect

  data class NavToHobbies(
    val targetUid: String
  ) : SecretSantaDetailUiSideEffect
}

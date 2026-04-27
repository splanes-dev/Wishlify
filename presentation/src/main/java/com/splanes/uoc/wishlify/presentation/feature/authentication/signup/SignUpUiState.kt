package com.splanes.uoc.wishlify.presentation.feature.authentication.signup

import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/** UI states rendered by the sign-up flow. */
sealed interface SignUpUiState {
  /** State that renders the sign-up form and its transient feedback. */
  data class SignUpForm(
    val isLoading: Boolean,
    val error: ErrorUiModel?,
    val emailInputError: String?,
    val usernameInputError: String?,
    val passwordInputError: String?,
  ) : SignUpUiState
}

/** One-off effects emitted by the sign-up flow. */
sealed interface SignUpUiSideEffect {
  /** Requests navigation to the home flow after a successful sign-up. */
  data object NavToHome : SignUpUiSideEffect
}

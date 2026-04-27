package com.splanes.uoc.wishlify.presentation.feature.authentication.signin

import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/** UI states rendered by the sign-in flow. */
sealed interface SignInUiState {
  /** State shown while the app attempts automatic sign-in with stored credentials. */
  data object AutoSignIn : SignInUiState

  /** State that renders the manual sign-in form and its transient feedback. */
  data class SignInForm(
    val isLoading: Boolean,
    val error: ErrorUiModel?,
    val emailInputError: String?,
    val passwordInputError: String?,
  ): SignInUiState
}

/** One-off effects emitted by the sign-in flow. */
sealed interface SignInUiSideEffect {
  /** Requests navigation to the home flow after a successful sign-in. */
  data object NavToHome : SignInUiSideEffect
}

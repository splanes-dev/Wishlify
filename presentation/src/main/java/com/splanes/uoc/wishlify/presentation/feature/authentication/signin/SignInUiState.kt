package com.splanes.uoc.wishlify.presentation.feature.authentication.signin

import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

sealed interface SignInUiState {
  data object AutoSignIn : SignInUiState

  data class SignInForm(
    val isLoading: Boolean,
    val error: ErrorUiModel?,
    val emailInputError: String?,
    val passwordInputError: String?,
  ): SignInUiState
}

sealed interface SignInUiSideEffect {
  data object NavToHome : SignInUiSideEffect
}
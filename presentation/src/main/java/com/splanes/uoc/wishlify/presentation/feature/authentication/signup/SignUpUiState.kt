package com.splanes.uoc.wishlify.presentation.feature.authentication.signup

import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

sealed interface SignUpUiState {
  data class SignUpForm(
    val isLoading: Boolean,
    val error: ErrorUiModel?,
    val emailInputError: String?,
    val usernameInputError: String?,
    val passwordInputError: String?,
  ) : SignUpUiState
}

sealed interface SignUpUiSideEffect {
  data object NavToHome : SignUpUiSideEffect
}
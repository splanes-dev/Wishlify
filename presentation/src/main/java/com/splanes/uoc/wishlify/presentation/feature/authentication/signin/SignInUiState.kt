package com.splanes.uoc.wishlify.presentation.feature.authentication.signin

sealed interface SignInUiState {
  data object AutoSignIn : SignInUiState

  data class SignInForm(
    val isLoading: Boolean,
    val error: Any?
  ): SignInUiState
}
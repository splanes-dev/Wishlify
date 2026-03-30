package com.splanes.uoc.wishlify.presentation.feature.authentication.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun SignInRoute(
  viewModel: SignInViewModel,
  onNavToSignUp: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    SignInUiState.AutoSignIn ->
      AutoSignInScreen()

    is SignInUiState.SignInForm ->
      SignInFormScreen(
        uiState = state,
        onNavToSignUp = onNavToSignUp
      )
  }
}
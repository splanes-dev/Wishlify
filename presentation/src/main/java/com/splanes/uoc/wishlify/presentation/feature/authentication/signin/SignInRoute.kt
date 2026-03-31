package com.splanes.uoc.wishlify.presentation.feature.authentication.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun SignInRoute(
  viewModel: SignInViewModel,
  onNavToSignUp: () -> Unit,
  onNavToHome: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        SignInUiSideEffect.NavToHome -> {
          onNavToHome()
        }
      }
    }
  }

  when (val state = uiState) {
    SignInUiState.AutoSignIn ->
      AutoSignInScreen()

    is SignInUiState.SignInForm ->
      SignInFormScreen(
        uiState = state,
        onDismissError = viewModel::onDismissError,
        onClearInputError = viewModel::onClearInputError,
        onSignIn = viewModel::onSignIn,
        onGoogleSignIn = viewModel::onGoogleSignIn,
        onSignUp = onNavToSignUp
      )
  }
}
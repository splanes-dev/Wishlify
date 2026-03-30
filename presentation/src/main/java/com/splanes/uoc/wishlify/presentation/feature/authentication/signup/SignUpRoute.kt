package com.splanes.uoc.wishlify.presentation.feature.authentication.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SignUpRoute(
  viewModel: SignUpViewModel,
  onNavToSignIn: () -> Unit,
  onNavToHome: () -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        SignUpUiSideEffect.NavToHome -> onNavToHome()
      }
    }
  }

  SignUpFormScreen(
    uiState = uiState,
    onDismissError = viewModel::onDismissError,
    onClearInputError = viewModel::onClearInputError,
    onSignUp = viewModel::onSignUp,
    onSignIn = onNavToSignIn
  )
}
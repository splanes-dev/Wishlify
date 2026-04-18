package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileUpdatePasswordRoute(
  viewModel: ProfileUpdatePasswordViewModel,
  onBack: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    ProfileUpdatePasswordUiState.Error ->
      ProfileUpdatePasswordErrorScreen(onBack = onBack)

    is ProfileUpdatePasswordUiState.Form ->
      ProfileUpdatePasswordScreen(
        uiState = state,
        onUpdate = viewModel::onUpdatePassword,
        onDismissError = viewModel::onDismissError,
        onClearInputError = viewModel::onClearInputError,
        onBack = onBack
      )

    ProfileUpdatePasswordUiState.Loading ->
      ProfileUpdatePasswordLoadingScreen(onBack = onBack)
  }
}
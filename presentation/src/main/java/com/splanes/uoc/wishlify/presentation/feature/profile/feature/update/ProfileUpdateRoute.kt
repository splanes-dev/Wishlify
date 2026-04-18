package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileUpdateRoute(
  viewModel: ProfileUpdateViewModel,
  onFinish: (result: Boolean) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        ProfileUpdateUiSideEffect.ProfileUpdated -> onFinish(true)
      }
    }
  }

  when (val state = uiState) {
    ProfileUpdateUiState.Error ->
      ProfileUpdateErrorScreen(onBack = { onFinish(false) })

    is ProfileUpdateUiState.Form ->
      ProfileUpdateScreen(
        uiState = state,
        onUpdate = viewModel::onUpdate,
        onClearInputError = viewModel::onClearInputError,
        onDismissError = viewModel::onDismissError,
        onBack = { onFinish(false) }
      )

    ProfileUpdateUiState.Loading ->
      ProfileUpdateLoadingScreen(onBack = { onFinish(false) })
  }
}
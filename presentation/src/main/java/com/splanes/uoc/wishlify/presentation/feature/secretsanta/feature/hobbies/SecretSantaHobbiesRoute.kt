package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SecretSantaHobbiesRoute(
  viewModel: SecretSantaHobbiesViewModel,
  onCancel: () -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    SecretSantaHobbiesUiState.Error ->
      SecretSantaHobbiesErrorScreen(onCancel = onCancel)

    is SecretSantaHobbiesUiState.Hobbies ->
      SecretSantaHobbiesScreen(
        uiState = state,
        onCancel = onCancel
      )

    SecretSantaHobbiesUiState.Loading ->
      SecretSantaHobbiesLoadingScreen(onCancel = onCancel)
  }
}
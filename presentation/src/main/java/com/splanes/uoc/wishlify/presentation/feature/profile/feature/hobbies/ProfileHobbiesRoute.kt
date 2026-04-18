package com.splanes.uoc.wishlify.presentation.feature.profile.feature.hobbies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileHobbiesRoute(
  viewModel: ProfileHobbiesViewModel,
  onBack: () -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        ProfileHobbiesUiSideEffect.HobbiesUpdated -> onBack()
      }
    }
  }

  when (val state = uiState) {
    ProfileHobbiesUiState.Error ->
      ProfileHobbiesErrorScreen(onBack = onBack)

    is ProfileHobbiesUiState.Hobbies ->
      ProfileHobbiesScreen(
        uiState = state,
        onUpdate = viewModel::onUpdateHobbies,
        onDismissError = viewModel::onDismissError,
        onBack = onBack
      )

    ProfileHobbiesUiState.Loading ->
      ProfileHobbiesLoadingScreen(onBack = onBack)
  }
}
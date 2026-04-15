package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SecretSantaDetailRoute(
  viewModel: SecretSantaDetailViewModel,
  onNavToEdit: (eventId: String) -> Unit,
  onNavToShareWishlist: (eventId: String) -> Unit,
  onNavBack: (update: Boolean) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        is SecretSantaDetailUiSideEffect.NavToEdit -> onNavToEdit(effect.event)
        is SecretSantaDetailUiSideEffect.NavToShareWishlist -> onNavToShareWishlist(effect.event)
      }
    }
  }

  when (val state = uiState) {
    is SecretSantaDetailUiState.Detail ->
      SecretSantaDetailScreen(
        uiState = state,
        onAction = viewModel::onAction,
        onDismissError = viewModel::onDismissError,
        onBack = { onNavBack(false) }
      )

    is SecretSantaDetailUiState.Error ->
      SecretSantaDetailErrorScreen(
        uiState = state,
        onBack = { onNavBack(false) }
      )

    is SecretSantaDetailUiState.Loading ->
      SecretSantaDetailLoadingScreen(
        uiState = state,
        onBack = { onNavBack(false) }
      )

  }
}
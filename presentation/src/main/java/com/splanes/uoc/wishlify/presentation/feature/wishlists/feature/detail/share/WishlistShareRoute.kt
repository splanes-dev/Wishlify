package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WishlistShareRoute(
  viewModel: WishlistShareViewModel,
  onNavToNewGroup: () -> Unit,
  onFinish: (result: Boolean) -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        WishlistShareUiSideEffect.WishlistShared -> onFinish(true)
      }
    }
  }

  when (val state = uiState) {
    WishlistShareUiState.Error ->
      WishlistShareErrorScreen(
        onCancel = { onFinish(false) }
      )

    WishlistShareUiState.Loading ->
      WishlistShareLoadingScreen(
        onCancel = { onFinish(false) }
      )

    is WishlistShareUiState.Share ->
      WishlistShareScreen(
        uiState = state,
        onShare = viewModel::onShare,
        onCreateGroup = onNavToNewGroup,
        onClearDateError = viewModel::onClearDateError,
        onDismissError = viewModel::onDismissError,
        onCancel = { onFinish(false) }
      )
  }
}
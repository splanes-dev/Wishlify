package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WishlistShareRoute(
  viewModel: WishlistShareViewModel,
  onNavToNewGroup: () -> Unit,
  onFinish: (shared: Boolean, wishlistName: String?) -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        is WishlistShareUiSideEffect.WishlistShared -> onFinish(true, effect.wishlistName)
      }
    }
  }

  when (val state = uiState) {
    WishlistShareUiState.Error ->
      WishlistShareErrorScreen(
        onCancel = { onFinish(false, null) }
      )

    WishlistShareUiState.Loading ->
      WishlistShareLoadingScreen(
        onCancel = { onFinish(false, null) }
      )

    is WishlistShareUiState.Share ->
      WishlistShareScreen(
        uiState = state,
        onShare = viewModel::onShare,
        onCreateGroup = onNavToNewGroup,
        onClearDateError = viewModel::onClearDateError,
        onDismissError = viewModel::onDismissError,
        onCancel = { onFinish(false, null) }
      )
  }
}
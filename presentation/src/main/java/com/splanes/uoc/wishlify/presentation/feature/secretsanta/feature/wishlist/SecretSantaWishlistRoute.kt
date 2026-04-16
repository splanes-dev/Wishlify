package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.wishlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SecretSantaWishlistRoute(
  viewModel: SecretSantaWishlistViewModel,
  onNavToShareWishlist: () -> Unit,
  onFinish: (Boolean) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        SecretSantaWishlistUiSideEffect.WishlistRemoved -> onFinish(true)
      }
    }
  }

  when (val state = uiState) {
    is SecretSantaWishlistUiState.Error ->
      SecretSantaWishlistErrorScreen(
        uiState = state,
        onCancel = { onFinish(false) }
      )

    is SecretSantaWishlistUiState.Listing ->
      SecretSantaWishlistScreen(
        uiState = state,
        onOpenItemDetailModal = viewModel::onOpenItemDetailModal,
        onCloseItemDetailModal = viewModel::onCloseItemDetailModal,
        onEditWishlist = onNavToShareWishlist,
        onDeleteWishlist = viewModel::onDeleteWishlist,
        onCancel = { onFinish(false) }
      )

    is SecretSantaWishlistUiState.Loading ->
      SecretSantaWishlistLoadingScreen(
        uiState = state,
        onCancel = { onFinish(false) }
      )

  }
}
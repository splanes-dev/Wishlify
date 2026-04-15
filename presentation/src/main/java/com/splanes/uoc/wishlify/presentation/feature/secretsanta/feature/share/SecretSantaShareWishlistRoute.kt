package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SecretSantaShareWishlistRoute(
  viewModel: SecretSantaShareWishlistViewModel,
  onFinish: (result: Boolean) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        SecretSantaShareWishlistUiSideEffect.WishlistShared -> onFinish(true)
      }
    }
  }

  when (val state = uiState) {
    is SecretSantaShareWishlistUiState.Empty ->
      SecretSantaShareWishlistEmptyScreen(
        uiState = state,
        onBack = viewModel::onCloseWishlist,
        onCancel = { onFinish(false) }
      )

    is SecretSantaShareWishlistUiState.Loading ->
      SecretSantaShareWishlistLoadingScreen(
        uiState = state,
        onBack = viewModel::onCloseWishlist,
        onCancel = { onFinish(false) }
      )

    is SecretSantaShareWishlistUiState.Wishlists ->
      SecretSantaShareWishlistScreen(
        uiState = state,
        onShareWishlist = viewModel::onShareWishlist,
        onOpenWishlist = viewModel::onOpenWishlist,
        onSelectWishlist = viewModel::onSelectWishlist,
        onCancel = { onFinish(false) }
      )

    is SecretSantaShareWishlistUiState.WishlistDetail ->
      SecretSantaShareWishlistDetailScreen(
        uiState = state,
        onOpenItemDetailModal = viewModel::onOpenItemDetailModal,
        onCloseItemDetailModal = viewModel::onCloseItemDetailModal,
        onBack = viewModel::onCloseWishlist,
        onCancel = { onFinish(false) }
      )
  }
}
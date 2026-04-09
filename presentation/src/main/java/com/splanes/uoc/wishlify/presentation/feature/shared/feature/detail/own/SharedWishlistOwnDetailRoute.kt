package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SharedWishlistOwnDetailRoute(
  viewModel: SharedWishlistOwnDetailViewModel,
  onBack: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is SharedWishlistOwnDetailUiState.Error ->
      SharedWishlistOwnDetailErrorScreen(
        uiState = state,
        onBack = onBack
      )

    is SharedWishlistOwnDetailUiState.Listing ->
      SharedWishlistOwnDetailScreen(
        uiState = state,
        onOpenItemDetail = viewModel::onOpenItemDetail,
        onDismissError = viewModel::onDismissError,
        onCloseItemDetailModal = viewModel::onCloseItemDetailModal,
        onBack = onBack,
      )

    is SharedWishlistOwnDetailUiState.Loading ->
      SharedWishlistOwnDetailLoadingScreen(
        uiState = state,
        onBack = onBack
      )
  }
}
package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SharedWishlistOwnDetailRoute(
  viewModel: SharedWishlistOwnDetailViewModel,
  onBack: (reload: Boolean) -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        SharedWishlistOwnDetailUiSideEffect.WishlistUnshared -> onBack(true)
      }
    }
  }

  when (val state = uiState) {
    is SharedWishlistOwnDetailUiState.Error ->
      SharedWishlistOwnDetailErrorScreen(
        uiState = state,
        onBack = { onBack(false) }
      )

    is SharedWishlistOwnDetailUiState.Listing ->
      SharedWishlistOwnDetailScreen(
        uiState = state,
        onOpenItemDetail = viewModel::onOpenItemDetail,
        onChangeProductFilters = viewModel::onChangeProductFilters,
        onDismissError = viewModel::onDismissError,
        onCloseItemDetailModal = viewModel::onCloseItemDetailModal,
        onBackToPrivates = viewModel::onBackToPrivate,
        onBack = { onBack(false) },
      )

    is SharedWishlistOwnDetailUiState.Loading ->
      SharedWishlistOwnDetailLoadingScreen(
        uiState = state,
        onBack = { onBack(false) }
      )
  }
}
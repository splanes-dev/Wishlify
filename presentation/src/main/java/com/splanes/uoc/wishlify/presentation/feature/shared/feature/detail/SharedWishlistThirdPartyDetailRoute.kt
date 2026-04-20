package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SharedWishlistThirdPartyDetailRoute(
  viewModel: SharedWishlistThirdPartyDetailViewModel,
  onBack: () -> Unit,
  onNavToChat: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is SharedWishlistThirdPartyDetailUiState.Error ->
      SharedWishlistThirdPartyDetailErrorScreen(
        uiState = state,
        onBack = onBack
      )

    is SharedWishlistThirdPartyDetailUiState.Listing ->
      SharedWishlistThirdPartyDetailScreen(
        uiState = state,
        onChatClick = onNavToChat,
        onDismissBanner = viewModel::onDismissBanner,
        onDismissError = viewModel::onDismissError,
        onItemAction = viewModel::onItemAction,
        onUpdateItemState = viewModel::onUpdateItemState,
        onOpenItemStateModal = viewModel::onOpenItemStateModal,
        onClearShareRequestError = viewModel::onClearShareRequestError,
        onCloseItemDetailModal = viewModel::onCloseItemDetailModal,
        onCloseItemStateModal = viewModel::onCloseItemStateModal,
        onBack = onBack,
      )

    is SharedWishlistThirdPartyDetailUiState.Loading ->
      SharedWishlistThirdPartyDetailLoadingScreen(
        uiState = state,
        onBack = onBack
      )
  }
}
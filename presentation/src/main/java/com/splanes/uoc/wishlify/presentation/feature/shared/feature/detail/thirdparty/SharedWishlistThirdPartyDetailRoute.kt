package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty

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
        onClearShareRequestError = viewModel::onClearShareRequestError,
        onCloseItemDetailModal = viewModel::onCloseItemDetailModal,
        onBack = onBack,
      )

    is SharedWishlistThirdPartyDetailUiState.Loading ->
      SharedWishlistThirdPartyDetailLoadingScreen(
        uiState = state,
        onBack = onBack
      )
  }
}
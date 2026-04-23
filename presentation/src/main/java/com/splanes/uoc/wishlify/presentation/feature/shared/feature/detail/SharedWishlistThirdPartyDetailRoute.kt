package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlistExternalAction
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlistExternalActionHandler

@Composable
fun SharedWishlistThirdPartyDetailRoute(
  viewModel: SharedWishlistThirdPartyDetailViewModel,
  externalActionHandler: SharedWishlistExternalActionHandler,
  onBack: () -> Unit,
  onNavToChat: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(externalActionHandler) {
    externalActionHandler.consume { action ->
      when (action) {
        is SharedWishlistExternalAction.OpenChatById -> {
          onNavToChat()
          externalActionHandler.clean()
        }

        else -> {
          // Nothing else to handle here
        }
      }
    }
  }

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
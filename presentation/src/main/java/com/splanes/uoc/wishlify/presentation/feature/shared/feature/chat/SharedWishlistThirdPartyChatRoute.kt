package com.splanes.uoc.wishlify.presentation.feature.shared.feature.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SharedWishlistThirdPartyChatRoute(
  viewModel: SharedWishlistThirdPartyChatViewModel,
  onBack: () -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is SharedWishlistThirdPartyChatUiState.Chat ->
      SharedWishlistThirdPartyChatScreen(
        uiState = state,
        onSendMessage = viewModel::onSendMessage,
        onLoadOlderMessages = viewModel::onLoadOlderMessages,
        onBack = onBack,
      )

    is SharedWishlistThirdPartyChatUiState.Error ->
      SharedWishlistThirdPartyChatErrorScreen(
        uiState = state,
        onBack = onBack
      )

    is SharedWishlistThirdPartyChatUiState.Loading ->
      SharedWishlistThirdPartyChatLoadingScreen(
        uiState = state,
        onBack = onBack
      )

    is SharedWishlistThirdPartyChatUiState.Empty ->
      SharedWishlistThirdPartyChatEmptyScreen(
        uiState = state,
        onSendMessage = viewModel::onSendMessage,
        onBack = onBack
      )
  }
}
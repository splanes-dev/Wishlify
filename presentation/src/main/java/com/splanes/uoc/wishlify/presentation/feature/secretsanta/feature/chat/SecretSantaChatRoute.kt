package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SecretSantaChatRoute(
  viewModel: SecretSantaChatViewModel,
  onBack: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is SecretSantaChatUiState.ChatAsGiver ->
      SecretSantaChatAsGiverScreen(
        uiState = state,
        onSendMessage = viewModel::onSendMessage,
        onLoadOlderMessages = viewModel::onLoadOlderMessages,
        onBack = onBack
      )

    is SecretSantaChatUiState.ChatAsReceiver ->
      SecretSantaChatAsReceiverScreen(
        uiState = state,
        onSendMessage = viewModel::onSendMessage,
        onLoadOlderMessages = viewModel::onLoadOlderMessages,
        onBack = onBack
      )

    is SecretSantaChatUiState.Empty ->
      SecretSantaChatEmptyScreen(
        uiState = state,
        onSendMessage = viewModel::onSendMessage,
        onBack = onBack
      )

    is SecretSantaChatUiState.Error ->
      SecretSantaChatErrorScreen(
        uiState = state,
        onBack = onBack
      )

    is SecretSantaChatUiState.Loading ->
      SecretSantaChatLoadingScreen(
        uiState = state,
        onBack = onBack
      )
  }
}
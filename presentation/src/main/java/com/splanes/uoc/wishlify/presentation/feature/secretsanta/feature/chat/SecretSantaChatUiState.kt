package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat

import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaChatMessage
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.model.SecretSantaChatType

sealed interface SecretSantaChatUiState {

  data class Loading(
    val type: SecretSantaChatType,
    val receiver: User.Basic?,
  ) : SecretSantaChatUiState

  data class Error(
    val type: SecretSantaChatType,
    val receiver: User.Basic?,
  ) : SecretSantaChatUiState

  data class Empty(
    val type: SecretSantaChatType,
    val receiver: User.Basic?,
  ) : SecretSantaChatUiState

  data class ChatAsGiver(
    val receiver: User.Basic,
    val messages: List<SecretSantaChatMessage>,
    val isLoading: Boolean,
    val canLoadOlderMessages: Boolean,
  ) : SecretSantaChatUiState

  data class ChatAsReceiver(
    val messages: List<SecretSantaChatMessage>,
    val isLoading: Boolean,
    val canLoadOlderMessages: Boolean,
  ) : SecretSantaChatUiState
}
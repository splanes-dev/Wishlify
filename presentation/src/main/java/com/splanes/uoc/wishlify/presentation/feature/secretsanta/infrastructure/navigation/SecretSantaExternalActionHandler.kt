package com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SecretSantaExternalActionHandler {
  private val actions = MutableStateFlow<SecretSantaExternalAction?>(null)

  fun dispatch(action: SecretSantaExternalAction) {
    actions.update { action }
  }

  suspend fun consume(consumer: suspend (SecretSantaExternalAction) -> Unit) {
    actions.collect { action ->
      action?.let { consumer(action) }
    }
  }

  fun clean() {
    actions.update { null }
  }
}

sealed interface SecretSantaExternalAction {
  data class JoinToParticipantsByToken(val token: String) : SecretSantaExternalAction
  data class OpenChatById(val secretSantaId: String, val chatType: String) : SecretSantaExternalAction
}
package com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory dispatcher used to forward external Secret Santa actions into the feature flow.
 */
class SecretSantaExternalActionHandler {
  private val actions = MutableStateFlow<SecretSantaExternalAction?>(null)

  /**
   * Publishes a new external action to be consumed by the feature.
   */
  fun dispatch(action: SecretSantaExternalAction) {
    actions.update { action }
  }

  /**
   * Collects and forwards dispatched actions to the provided consumer.
   */
  suspend fun consume(consumer: suspend (SecretSantaExternalAction) -> Unit) {
    actions.collect { action ->
      action?.let { consumer(action) }
    }
  }

  /**
   * Clears the currently buffered external action once it has been handled.
   */
  fun clean() {
    actions.update { null }
  }
}

/**
 * External entry points that can redirect the user into a specific Secret Santa flow.
 */
sealed interface SecretSantaExternalAction {
  data class JoinToParticipantsByToken(val token: String) : SecretSantaExternalAction
  data class OpenDetailById(val secretSantaId: String) : SecretSantaExternalAction

  data class OpenChatById(val secretSantaId: String, val chatType: String) : SecretSantaExternalAction
}

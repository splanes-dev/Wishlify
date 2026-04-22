package com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class SecretSantaExternalActionHandler {
  private val channel = Channel<SecretSantaExternalAction>(capacity = Channel.BUFFERED)
  private val actions = channel.receiveAsFlow()

  fun dispatch(action: SecretSantaExternalAction) {
    channel.trySend(action)
  }

  suspend fun consume(consumer: suspend (SecretSantaExternalAction) -> Unit) {
    actions.collect { action -> consumer(action) }
  }
}

sealed interface SecretSantaExternalAction {
  data class JoinToParticipantsByToken(val token: String) : SecretSantaExternalAction
}
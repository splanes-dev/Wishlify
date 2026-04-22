package com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class SharedWishlistExternalActionHandler {
  private val channel = Channel<SharedWishlistExternalAction>(capacity = Channel.BUFFERED)
  private val actions = channel.receiveAsFlow()

  fun dispatch(action: SharedWishlistExternalAction) {
    channel.trySend(action)
  }

  suspend fun consume(consumer: suspend (SharedWishlistExternalAction) -> Unit) {
    actions.collect { action -> consumer(action) }
  }
}

sealed interface SharedWishlistExternalAction {
  data class JoinToParticipantsByToken(val token: String) : SharedWishlistExternalAction
}
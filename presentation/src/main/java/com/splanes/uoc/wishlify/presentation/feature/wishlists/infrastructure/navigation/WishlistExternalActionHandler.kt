package com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class WishlistExternalActionHandler {

  private val channel = Channel<WishlistExternalAction>(capacity = Channel.BUFFERED)
  private val actions = channel.receiveAsFlow()

  fun dispatch(action: WishlistExternalAction) {
    channel.trySend(action)
  }

  suspend fun consume(consumer: suspend (WishlistExternalAction) -> Unit) {
    actions.collect { action -> consumer(action) }
  }
}

sealed interface WishlistExternalAction {
  data class JoinToEditorByToken(val token: String): WishlistExternalAction
  data class NewItemByUrl(val url: String): WishlistExternalAction
  data class NewItemByUri(val uri: String): WishlistExternalAction
}
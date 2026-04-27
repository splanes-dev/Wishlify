package com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * In-memory dispatcher used to forward external wishlist actions into the feature flow.
 */
class WishlistExternalActionHandler {

  private val channel = Channel<WishlistExternalAction>(capacity = Channel.BUFFERED)
  private val actions = channel.receiveAsFlow()

  /**
   * Publishes a new external action to be consumed by the feature.
   */
  fun dispatch(action: WishlistExternalAction) {
    channel.trySend(action)
  }

  /**
   * Collects and forwards dispatched actions to the provided consumer.
   */
  suspend fun consume(consumer: suspend (WishlistExternalAction) -> Unit) {
    actions.collect { action -> consumer(action) }
  }
}

/**
 * External entry points that can redirect the user into a specific wishlist flow.
 */
sealed interface WishlistExternalAction {
  data class JoinToEditorByToken(val token: String): WishlistExternalAction
  data class NewItemByUrl(val url: String): WishlistExternalAction
  data class NewItemByUri(val uri: String): WishlistExternalAction
}

package com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory dispatcher used to forward external shared wishlist actions into the feature flow.
 */
class SharedWishlistExternalActionHandler {

  private val actions: MutableStateFlow<SharedWishlistExternalAction?> = MutableStateFlow(null)

  /**
   * Publishes a new external action to be consumed by the feature.
   */
  fun dispatch(action: SharedWishlistExternalAction) {
    actions.update { action }
  }

  /**
   * Collects and forwards dispatched actions to the provided consumer.
   */
  suspend fun consume(consumer: suspend (SharedWishlistExternalAction) -> Unit) {
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
 * External entry points that can redirect the user into a specific shared wishlist flow.
 */
sealed interface SharedWishlistExternalAction {
  data class JoinToParticipantsByToken(val token: String) : SharedWishlistExternalAction
  data class OpenDetailById(val id: String) : SharedWishlistExternalAction
  data class OpenChatById(val id: String) : SharedWishlistExternalAction
}

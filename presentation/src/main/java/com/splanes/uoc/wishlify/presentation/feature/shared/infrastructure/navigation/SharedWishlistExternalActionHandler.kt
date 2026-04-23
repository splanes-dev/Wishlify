package com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SharedWishlistExternalActionHandler {

  private val actions: MutableStateFlow<SharedWishlistExternalAction?> = MutableStateFlow(null)

  fun dispatch(action: SharedWishlistExternalAction) {
    actions.update { action }
  }

  suspend fun consume(consumer: suspend (SharedWishlistExternalAction) -> Unit) {
    actions.collect { action ->
      action?.let { consumer(action) }
    }
  }

  fun clean() {
    actions.update { null }
  }
}

sealed interface SharedWishlistExternalAction {
  data class JoinToParticipantsByToken(val token: String) : SharedWishlistExternalAction
  data class OpenDetailById(val id: String) : SharedWishlistExternalAction
  data class OpenChatById(val id: String) : SharedWishlistExternalAction
}
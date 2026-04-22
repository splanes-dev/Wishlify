package com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ExternalActionHandler {

  private val actionsSharedFlow = MutableSharedFlow<Action?>(replay = 1, extraBufferCapacity = 1)
  private val actions = actionsSharedFlow.asSharedFlow()

  fun dispatch(action: Action) {
    actionsSharedFlow.tryEmit(action)
  }

  suspend fun consume(consumer: suspend (Action) -> Unit) {
    actions.collect { action ->
      action?.let { consumer(action) }
    }
  }

  fun clear() {
    actionsSharedFlow.tryEmit(null)
  }
}
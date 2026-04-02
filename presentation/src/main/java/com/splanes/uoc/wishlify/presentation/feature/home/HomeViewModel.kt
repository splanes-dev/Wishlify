package com.splanes.uoc.wishlify.presentation.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.session.model.SessionState
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetSessionStateFlowUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(
  private val getSessionStateFlowUseCase: GetSessionStateFlowUseCase
) : ViewModel() {

  private val uiSideEffectChannel = Channel<HomeUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  private var observeJob: Job? = null

  fun observeSessionState() {
    if (observeJob?.isActive == true) return

    observeJob = viewModelScope.launch {
      getSessionStateFlowUseCase().collect { state ->
        if (state == SessionState.SignedOut) {
          uiSideEffectChannel.send(HomeUiSideEffect.NoSession)
        }
      }
    }
  }

  fun cancelSessionStateObserver() {
    observeJob?.cancel()
  }
}
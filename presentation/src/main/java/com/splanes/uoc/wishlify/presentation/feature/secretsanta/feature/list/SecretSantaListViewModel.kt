package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaEventsUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SecretSantaListViewModel(
  private val fetchSecretSantaEventsUseCase: FetchSecretSantaEventsUseCase,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchSecretSantaEvents() }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  fun onNewEventResult() {
    viewModelScope.launch { fetchSecretSantaEvents() }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private suspend fun fetchSecretSantaEvents() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    fetchSecretSantaEventsUseCase()
      .onSuccess { events ->
        viewModelState.update { state ->
          state.copy(
            events = events,
            isLoadingFullscreen = false,
          )
        }
      }
      .onFailure { error ->
        viewModelState.update { state ->
          state.copy(
            error = error,
            isLoadingFullscreen = false,
          )
        }
      }
  }

  private data class ViewModelState(
    val events: List<SecretSantaEvent> = emptyList(),
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    fun toUiState(errorUiMapper: ErrorUiMapper) = when {
      isLoadingFullscreen ->
        SecretSantaListUiState.Loading

      events.isEmpty() ->
        SecretSantaListUiState.Empty(
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )

      else ->
        SecretSantaListUiState.Events(
          events = events.sorted(),
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )
    }

    private fun List<SecretSantaEvent>.sorted() = sortedWith(
      compareByDescending { it.deadline }
    )
  }
}
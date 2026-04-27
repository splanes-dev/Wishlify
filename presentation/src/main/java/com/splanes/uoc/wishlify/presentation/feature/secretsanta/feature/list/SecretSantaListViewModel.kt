package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.notifications.usecase.IsPermissionModalVisibleUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.AddEventParticipantFromLinkUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaEventsUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Coordinates the Secret Santa events list, including link-based participant joins and refreshes
 * after nested flows.
 */
class SecretSantaListViewModel(
  private val fetchSecretSantaEventsUseCase: FetchSecretSantaEventsUseCase,
  private val addEventParticipantFromLinkUseCase: AddEventParticipantFromLinkUseCase,
  private val isPermissionModalVisibleUseCase: IsPermissionModalVisibleUseCase,
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

  /**
   * Adds the current user to an event using an invitation token and refreshes the list.
   */
  fun onJoinToParticipantsByToken(token: String) {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    viewModelScope.launch {
      addEventParticipantFromLinkUseCase(token)
      fetchSecretSantaEvents()
    }
  }

  /**
   * Returns the event already loaded in memory, waiting briefly until the initial fetch completes.
   */
  suspend fun fetchSecretSantaEventById(id: String): SecretSantaEvent {
    val currentState = viewModelState.value
    if (currentState.events.isNotEmpty()) {
      return currentState.events.first { it.id == id }
    } else {
      delay(250.milliseconds)
      return fetchSecretSantaEventById(id)
    }
  }

  /**
   * Refreshes the events list after returning from the creation flow.
   */
  fun onNewEventResult() {
    viewModelScope.launch { fetchSecretSantaEvents() }
  }

  /**
   * Clears the current UI error.
   */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /**
   * Loads the current Secret Santa events and resolves whether the notification permission modal
   * should be displayed.
   */
  private suspend fun fetchSecretSantaEvents() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    fetchSecretSantaEventsUseCase()
      .onSuccess { events ->
        viewModelState.update { state ->
          state.copy(
            events = events,
            isPermissionModalVisible = isPermissionModalVisibleUseCase(),
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
    val isPermissionModalVisible: Boolean = false,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    /**
     * Maps internal state to the screen representation shown by the list UI.
     */
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
          isPermissionModalVisible = isPermissionModalVisible,
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )
    }

    /**
     * Sorts events by deadline so the closest events appear first.
     */
    private fun List<SecretSantaEvent>.sorted() = sortedWith(
      compareByDescending { it.deadline }
    )
  }
}

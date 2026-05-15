package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.GiftSuggestion
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.GetAiGiftSuggestionsUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserHobbiesUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.mapper.GiftSuggestionUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Loads the hobbies profile of the Secret Santa receiver.
 */
class SecretSantaHobbiesViewModel(
  private val eventId: String,
  private val targetUid: String,
  private val fetchUserHobbiesUseCase: FetchUserHobbiesUseCase,
  private val getAiGiftSuggestionsUseCase: GetAiGiftSuggestionsUseCase,
  private val giftSuggestionUiMapper: GiftSuggestionUiMapper,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchUserHobbies() }
    .map { state -> state.toUiState(errorUiMapper, giftSuggestionUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper, giftSuggestionUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  fun onGenerateSuggestions() {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      val result = getAiGiftSuggestionsUseCase(
        target = targetUid,
        eventId = eventId,
      )
      viewModelState.update { state ->
        state.copy(
          suggestions = result.getOrDefault(emptyList()),
          isSuggestionsModalOpen = result.isSuccess,
          isLoading = false,
          error = result.exceptionOrNull()
        )
      }
    }
  }

  fun onCloseSuggestionModal() {
    viewModelState.update { state ->
      state.copy(
        suggestions = emptyList(),
        isSuggestionsModalOpen = false
      )
    }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /**
   * Loads the hobbies profile associated with the current target user.
   */
  private suspend fun fetchUserHobbies() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchUserHobbiesUseCase(targetUid)
    viewModelState.update { state ->
      state.copy(
        targetUser = result.getOrNull(),
        isLoadingFullscreen = false,
      )
    }
  }

  private data class ViewModelState(
    val targetUser: User.HobbiesProfile? = null,
    val suggestions: List<GiftSuggestion> = emptyList(),
    val isSuggestionsModalOpen: Boolean = false,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    /**
     * Maps internal state to the hobbies UI contract.
     */
    fun toUiState(errorUiMapper: ErrorUiMapper, giftSuggestionUiMapper: GiftSuggestionUiMapper) = when {
      isLoadingFullscreen -> SecretSantaHobbiesUiState.Loading
      targetUser == null -> SecretSantaHobbiesUiState.Error
      else -> SecretSantaHobbiesUiState.Hobbies(
        user = targetUser,
        suggestions = giftSuggestionUiMapper.map(suggestions),
        isSuggestionsModalOpen = isSuggestionsModalOpen,
        isLoading = isLoading,
        error = error?.run(errorUiMapper::map)
      )
    }
  }
}

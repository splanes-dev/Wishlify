package com.splanes.uoc.wishlify.presentation.feature.profile.feature.hobbies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserHobbiesUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.UpdateUserProfileUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates hobbies administration for the current user profile.
 */
class ProfileHobbiesViewModel(
  private val fetchUserHobbiesUseCase: FetchUserHobbiesUseCase,
  private val updateUserProfileUseCase: UpdateUserProfileUseCase,
  private val errorUiMapper: ErrorUiMapper,
): ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchUserProfile() }
    .map { state ->
      state.toUiState(
        errorUiMapper = errorUiMapper,
      )
    }
    .stateIn(
      initialValue = viewModelState.value.toUiState(
        errorUiMapper = errorUiMapper,
      ),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<ProfileHobbiesUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  /**
   * Updates the hobbies settings of the current user.
   */
  fun onUpdateHobbies(enabled: Boolean, hobbies: List<String>) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      val user = viewModelState.value.user ?: error("No user, this should not happen")
      val request = UpdateProfileRequest.Hobbies(
        user = user,
        enabled = enabled,
        values = hobbies
      )

      updateUserProfileUseCase(request)
        .onSuccess {
          viewModelState.update { state -> state.copy(isLoading = false) }
          uiSideEffectChannel.send(ProfileHobbiesUiSideEffect.HobbiesUpdated)
        }
        .onFailure { error ->
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              error = error,
            )
          }
        }
    }
  }

  /**
   * Clears the current UI error.
   */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /**
   * Loads the hobbies profile of the current user.
   */
  private suspend fun fetchUserProfile() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchUserHobbiesUseCase()
    viewModelState.update { state ->
      state.copy(
        user = result.getOrNull(),
        isLoadingFullscreen = false,
      )
    }
  }

  private data class ViewModelState(
    val user: User.HobbiesProfile? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    /**
     * Maps internal state to the hobbies UI contract.
     */
    fun toUiState(errorUiMapper: ErrorUiMapper) = when {
      isLoadingFullscreen -> ProfileHobbiesUiState.Loading
      user == null -> ProfileHobbiesUiState.Error
      else -> ProfileHobbiesUiState.Hobbies(
        user = user,
        isLoading = isLoading,
        error = error?.let(errorUiMapper::map)
      )
    }
  }
}

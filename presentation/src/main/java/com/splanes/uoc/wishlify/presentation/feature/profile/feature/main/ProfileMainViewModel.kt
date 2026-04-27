package com.splanes.uoc.wishlify.presentation.feature.profile.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignOutUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchBasicUserProfileUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates the main profile screen, including profile loading and sign-out.
 */
class ProfileMainViewModel(
  private val fetchBasicUserProfileUseCase: FetchBasicUserProfileUseCase,
  private val signOutUseCase: SignOutUseCase,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())
  val uiState = viewModelState.asStateFlow()
    .onStart { fetchUserProfile() }
    .map { state -> state.toUiState(errorUiMapper = errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper = errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  /**
   * Signs the current user out from the app.
   */
  fun onSignOut() {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      signOutUseCase()
      viewModelState.update { state -> state.copy(isLoading = false) }
    }
  }

  /**
   * Reloads the profile after returning from the profile update flow.
   */
  fun onProfileUpdated() {
    viewModelScope.launch { fetchUserProfile() }
  }

  /**
   * Loads the current basic profile.
   */
  private suspend fun fetchUserProfile() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchBasicUserProfileUseCase()
    viewModelState.update { state ->
      state.copy(
        isLoadingFullscreen = false,
        user = result.getOrNull()
      )
    }
  }

  private data class ViewModelState(
    val user: User.BasicProfile? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    /**
     * Maps internal state to the main profile UI contract.
     */
    fun toUiState(errorUiMapper: ErrorUiMapper) = when {
      isLoadingFullscreen -> ProfileMainUiState.Loading
      user == null -> ProfileMainUiState.Error
      else ->
        ProfileMainUiState.Profile(
          user = user,
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )
    }
  }
}

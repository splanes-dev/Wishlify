package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserHobbiesUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SecretSantaHobbiesViewModel(
  private val targetUid: String,
  private val fetchUserHobbiesUseCase: FetchUserHobbiesUseCase,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchUserHobbies() }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

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
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    fun toUiState(errorUiMapper: ErrorUiMapper) = when {
      isLoadingFullscreen -> SecretSantaHobbiesUiState.Loading
      targetUser == null -> SecretSantaHobbiesUiState.Error
      else -> SecretSantaHobbiesUiState.Hobbies(
        user = targetUser,
        isLoading = isLoading,
        error = error?.run(errorUiMapper::map)
      )
    }
  }
}
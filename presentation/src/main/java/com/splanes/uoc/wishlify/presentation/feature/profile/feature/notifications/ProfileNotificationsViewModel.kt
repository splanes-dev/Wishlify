package com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserNotificationsUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.UpdateUserProfileUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.model.UserProfileNotificationsForm
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
 * Coordinates notification preferences administration for the current user.
 */
class ProfileNotificationsViewModel(
  private val fetchUserNotificationsUseCase: FetchUserNotificationsUseCase,
  private val updateUserProfileUseCase: UpdateUserProfileUseCase,
  private val errorUiMapper: ErrorUiMapper,
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

  private val uiSideEffectChannel = Channel<ProfileNotificationsUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  /**
   * Updates the notification preferences of the current user.
   */
  fun onUpdateNotifications(form: UserProfileNotificationsForm) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      val user = viewModelState.value.user ?: error("No user found, this should not happen")
      val request = UpdateProfileRequest.Notifications(
        user = user,
        sharedWishlistChat = form.sharedWishlistChat,
        sharedWishlistUpdates = form.sharedWishlistUpdates,
        sharedWishlistsDeadlineReminders = form.sharedWishlistsDeadlineReminders,
        secretSantaChat = form.secretSantaChat,
        secretSantaDeadlineReminders = form.secretSantaDeadlineReminders,
      )
      updateUserProfileUseCase(request)
        .onSuccess {
          viewModelState.update { state -> state.copy(isLoading = false) }
          uiSideEffectChannel.send(ProfileNotificationsUiSideEffect.NotificationsUpdated)
        }
        .onFailure { error ->
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              error = error
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
   * Loads the notification preferences profile and projects it into the editable form model.
   */
  private suspend fun fetchUserProfile() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchUserNotificationsUseCase()
    viewModelState.update { state ->
      val notifications = result.getOrNull()?.notificationPermissions
      state.copy(
        user = result.getOrNull(),
        form = state.form.copy(
          sharedWishlistChat = notifications?.sharedWishlistChat == true,
          sharedWishlistUpdates = notifications?.sharedWishlistUpdates == true,
          sharedWishlistsDeadlineReminders = notifications?.sharedWishlistsDeadlineReminders == true,
          secretSantaChat = notifications?.secretSantaChat == true,
          secretSantaDeadlineReminders = notifications?.secretSantaDeadlineReminders == true,
        ),
        isLoadingFullscreen = false,
      )
    }
  }

  private data class ViewModelState(
    val user: User.NotificationsProfile? = null,
    val form: UserProfileNotificationsForm = UserProfileNotificationsForm(),
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    /**
     * Maps internal state to the notification preferences UI contract.
     */
    fun toUiState(
      errorUiMapper: ErrorUiMapper,
    ) = when {
      isLoadingFullscreen -> ProfileNotificationsUiState.Loading
      user == null -> ProfileNotificationsUiState.Error
      else ->
        ProfileNotificationsUiState.Notifications(
          user = user,
          form = form,
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )
    }
  }
}

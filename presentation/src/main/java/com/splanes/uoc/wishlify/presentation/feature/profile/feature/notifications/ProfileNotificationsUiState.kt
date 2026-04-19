package com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.model.UserProfileNotificationsForm

sealed interface ProfileNotificationsUiState {
  data object Loading : ProfileNotificationsUiState
  data object Error : ProfileNotificationsUiState
  data class Notifications(
    val user: User.NotificationsProfile,
    val form: UserProfileNotificationsForm,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ) : ProfileNotificationsUiState
}

sealed interface ProfileNotificationsUiSideEffect {
  data object NotificationsUpdated : ProfileNotificationsUiSideEffect
}
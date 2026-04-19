package com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileNotificationsRoute(
  viewModel: ProfileNotificationsViewModel,
  onBack: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        ProfileNotificationsUiSideEffect.NotificationsUpdated -> onBack()
      }
    }
  }

  when (val state = uiState) {
    ProfileNotificationsUiState.Error ->
      ProfileNotificationsErrorScreen(onBack = onBack)

    ProfileNotificationsUiState.Loading ->
      ProfileNotificationsLoadingScreen(onBack = onBack)

    is ProfileNotificationsUiState.Notifications ->
      ProfileNotificationsScreen(
        uiState = state,
        onUpdate = viewModel::onUpdateNotifications,
        onDismissError = viewModel::onDismissError,
        onBack = onBack
      )
  }
}
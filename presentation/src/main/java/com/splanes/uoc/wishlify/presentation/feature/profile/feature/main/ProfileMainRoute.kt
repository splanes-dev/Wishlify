package com.splanes.uoc.wishlify.presentation.feature.profile.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileMainRoute(
  viewModel: ProfileMainViewModel,
  onNavToUpdateProfile: () -> Unit,
  onNavToChangePassword: () -> Unit,
  onNavToAdminNotifications: () -> Unit,
  onNavToStore: () -> Unit,
  onNavToHobbies: () -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    ProfileMainUiState.Error -> ProfileMainErrorScreen()
    ProfileMainUiState.Loading -> ProfileMainLoadingScreen()
    is ProfileMainUiState.Profile ->
      ProfileMainScreen(
        uiState = state,
        onSignOut = viewModel::onSignOut,
        onUpdateProfile = onNavToUpdateProfile,
        onChangePassword = onNavToChangePassword,
        onAdminNotifications = onNavToAdminNotifications,
        onAdminStore = onNavToStore,
        onAdminHobbies = onNavToHobbies,
      )
  }
}
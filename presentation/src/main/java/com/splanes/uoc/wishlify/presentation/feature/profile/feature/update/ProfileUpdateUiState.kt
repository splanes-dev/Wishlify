package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateForm
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateFormUiErrors

sealed interface ProfileUpdateUiState {

  data object Loading : ProfileUpdateUiState
  data object Error : ProfileUpdateUiState
  data class Form(
    val user: User.BasicProfile,
    val form: UserProfileUpdateForm,
    val formErrors: UserProfileUpdateFormUiErrors,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : ProfileUpdateUiState
}

sealed interface ProfileUpdateUiSideEffect {
  data object ProfileUpdated : ProfileUpdateUiSideEffect
}
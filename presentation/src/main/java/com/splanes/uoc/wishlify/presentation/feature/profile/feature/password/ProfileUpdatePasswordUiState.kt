package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordForm
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordFormUiErrors

sealed interface ProfileUpdatePasswordUiState {

  data object Loading : ProfileUpdatePasswordUiState
  data object Error : ProfileUpdatePasswordUiState
  data class Form(
    val user: User.BasicProfile,
    val form: UserProfileUpdatePasswordForm,
    val formErrors: UserProfileUpdatePasswordFormUiErrors,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : ProfileUpdatePasswordUiState
}
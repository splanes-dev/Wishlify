package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model

data class UserProfileUpdatePasswordForm(
  val currentPassword: String = "",
  val newPassword: String = "",
  val newPasswordConfirm: String = ""
) {

  enum class Input {
    CurrentPassword,
    NewPassword,
    NewPasswordConfirm,
  }
}

data class UserProfileUpdatePasswordFormUiErrors(
  val currentPassword: String?,
  val newPassword: String?,
  val newPasswordConfirm: String?,
)
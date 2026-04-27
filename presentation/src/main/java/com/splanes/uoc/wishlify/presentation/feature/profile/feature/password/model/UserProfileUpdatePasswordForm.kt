package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model

/**
 * Form data collected during password update.
 */
data class UserProfileUpdatePasswordForm(
  val currentPassword: String = "",
  val newPassword: String = "",
  val newPasswordConfirm: String = ""
) {

  /**
   * Inputs whose validation errors can be cleared independently.
   */
  enum class Input {
    CurrentPassword,
    NewPassword,
    NewPasswordConfirm,
  }
}

/**
 * UI-ready validation messages for the password update form.
 */
data class UserProfileUpdatePasswordFormUiErrors(
  val currentPassword: String?,
  val newPassword: String?,
  val newPasswordConfirm: String?,
)

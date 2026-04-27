package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model

/**
 * Typed validation errors produced while validating the password update form.
 */
data class UserProfileUpdatePasswordFormErrors(
  val currentPassword: UserProfileUpdatePasswordFormPasswordError? = null,
  val newPassword: UserProfileUpdateNewPasswordFormPasswordError? = null,
  val newPasswordConfirm: UserProfileUpdateNewPasswordConfirmFormPasswordError? = null,
)

/**
 * Marker interface for password update form validation errors.
 */
sealed interface UserProfileUpdatePasswordFormError

/**
 * Validation errors for the current password field.
 */
sealed interface UserProfileUpdatePasswordFormPasswordError : UserProfileUpdatePasswordFormError {
  data object Blank : UserProfileUpdatePasswordFormPasswordError
}

/**
 * Validation errors for the new password field.
 */
sealed interface UserProfileUpdateNewPasswordFormPasswordError : UserProfileUpdatePasswordFormError {
  data object Blank : UserProfileUpdateNewPasswordFormPasswordError
  data object Weak : UserProfileUpdateNewPasswordFormPasswordError
}

/**
 * Validation errors for the new password confirmation field.
 */
sealed interface UserProfileUpdateNewPasswordConfirmFormPasswordError : UserProfileUpdatePasswordFormError {
  data object Blank : UserProfileUpdateNewPasswordConfirmFormPasswordError
  data object NotMatch : UserProfileUpdateNewPasswordConfirmFormPasswordError
}

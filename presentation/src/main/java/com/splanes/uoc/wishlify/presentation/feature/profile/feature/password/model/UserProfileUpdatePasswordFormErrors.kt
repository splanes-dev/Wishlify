package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model

data class UserProfileUpdatePasswordFormErrors(
  val currentPassword: UserProfileUpdatePasswordFormPasswordError? = null,
  val newPassword: UserProfileUpdateNewPasswordFormPasswordError? = null,
  val newPasswordConfirm: UserProfileUpdateNewPasswordConfirmFormPasswordError? = null,
)

sealed interface UserProfileUpdatePasswordFormError
sealed interface UserProfileUpdatePasswordFormPasswordError : UserProfileUpdatePasswordFormError {
  data object Blank : UserProfileUpdatePasswordFormPasswordError
}

sealed interface UserProfileUpdateNewPasswordFormPasswordError : UserProfileUpdatePasswordFormError {
  data object Blank : UserProfileUpdateNewPasswordFormPasswordError
  data object Weak : UserProfileUpdateNewPasswordFormPasswordError
}

sealed interface UserProfileUpdateNewPasswordConfirmFormPasswordError : UserProfileUpdatePasswordFormError {
  data object Blank : UserProfileUpdateNewPasswordConfirmFormPasswordError
  data object NotMatch : UserProfileUpdateNewPasswordConfirmFormPasswordError
}
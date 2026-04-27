package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model

/**
 * Typed validation errors produced while validating the profile update form.
 */
data class UserProfileUpdateFormErrors(
  val username: UserProfileUpdateFormNameError? = null,
  val email: UserProfileUpdateFormEmailError? = null,
)

/**
 * Marker interface for profile update form validation errors.
 */
sealed interface UserProfileUpdateFormError

/**
 * Validation errors for the username field.
 */
sealed interface UserProfileUpdateFormNameError : UserProfileUpdateFormError {
  data object Blank : UserProfileUpdateFormNameError
  data object Length : UserProfileUpdateFormNameError
  data object InvalidChars : UserProfileUpdateFormNameError
}

/**
 * Validation errors for the email field.
 */
sealed interface UserProfileUpdateFormEmailError : UserProfileUpdateFormError {
  data object Invalid : UserProfileUpdateFormEmailError
}

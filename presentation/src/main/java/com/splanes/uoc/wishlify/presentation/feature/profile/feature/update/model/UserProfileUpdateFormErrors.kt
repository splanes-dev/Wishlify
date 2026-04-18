package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model

data class UserProfileUpdateFormErrors(
  val username: UserProfileUpdateFormNameError? = null,
  val email: UserProfileUpdateFormEmailError? = null,
)

sealed interface UserProfileUpdateFormError
sealed interface UserProfileUpdateFormNameError : UserProfileUpdateFormError {
  data object Blank : UserProfileUpdateFormNameError
  data object Length : UserProfileUpdateFormNameError
  data object InvalidChars : UserProfileUpdateFormNameError
}

sealed interface UserProfileUpdateFormEmailError : UserProfileUpdateFormError {
  data object Invalid : UserProfileUpdateFormEmailError
}
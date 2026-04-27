package com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model

/** Marker for the validation errors that can appear in the sign-up form. */
sealed interface SignUpFormError

/** Validation errors associated with the email input of the sign-up form. */
sealed interface EmailSignUpFormError : SignUpFormError {
  data object Invalid : EmailSignUpFormError
}

/** Validation errors associated with the username input of the sign-up form. */
sealed interface UsernameSignUpFormError : SignUpFormError {
  data object Blank : UsernameSignUpFormError
  data object Length : UsernameSignUpFormError
  data object InvalidChars : UsernameSignUpFormError
}

/** Validation errors associated with the password input of the sign-up form. */
sealed interface PasswordSignUpFormError : SignUpFormError {
  data object Blank : PasswordSignUpFormError
  data object Weak : PasswordSignUpFormError
}

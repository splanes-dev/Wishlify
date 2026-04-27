package com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model

/** Marker for the validation errors that can appear in the sign-in form. */
sealed interface SignInFormError

/** Validation errors associated with the email input of the sign-in form. */
sealed interface EmailSignInFormError : SignInFormError {
  data object Invalid : EmailSignInFormError
}
/** Validation errors associated with the password input of the sign-in form. */
sealed interface PasswordSignInFormError : SignInFormError {
  data object Blank : PasswordSignInFormError
}

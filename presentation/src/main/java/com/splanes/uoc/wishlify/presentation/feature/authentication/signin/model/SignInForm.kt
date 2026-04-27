package com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model

/** User input collected by the sign-in screen before validation and submission. */
data class SignInForm(
  val email: String,
  val password: String,
) {
  /** Identifies the editable fields of the sign-in form. */
  enum class Input {
    Email,
    Password,
  }
}

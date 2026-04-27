package com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model

/** User input collected by the sign-up screen before validation and submission. */
data class SignUpForm(
  val email: String,
  val username: String,
  val password: String,
) {
  /** Identifies the editable fields of the sign-up form. */
  enum class Input {
    Email,
    Username,
    Password,
  }
}

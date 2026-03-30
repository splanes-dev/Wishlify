package com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model

data class SignUpForm(
  val email: String,
  val username: String,
  val password: String,
) {
  enum class Input {
    Email,
    Username,
    Password,
  }
}

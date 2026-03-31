package com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model

data class SignInForm(
  val email: String,
  val password: String,
) {
  enum class Input {
    Email,
    Password,
  }
}
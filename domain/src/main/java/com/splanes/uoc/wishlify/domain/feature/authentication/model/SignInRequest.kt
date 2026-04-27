package com.splanes.uoc.wishlify.domain.feature.authentication.model

/**
 * Input required to authenticate a user with email and password.
 */
data class SignInRequest(
  val email: String,
  val password: String,
)

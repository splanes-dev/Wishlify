package com.splanes.uoc.wishlify.domain.feature.authentication.model

/**
 * Input required to register a new user with local credentials.
 */
data class SignUpRequest(
  val username: String,
  val email: String,
  val password: String,
)

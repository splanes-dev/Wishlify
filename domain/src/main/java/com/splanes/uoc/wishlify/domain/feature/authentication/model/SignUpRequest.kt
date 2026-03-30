package com.splanes.uoc.wishlify.domain.feature.authentication.model

data class SignUpRequest(
  val username: String,
  val email: String,
  val password: String,
)

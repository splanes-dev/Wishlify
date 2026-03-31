package com.splanes.uoc.wishlify.domain.feature.authentication.model

data class SignInRequest(
  val email: String,
  val password: String,
)
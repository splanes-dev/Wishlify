package com.splanes.uoc.wishlify.domain.feature.authentication.model

/**
 * Locally persisted credentials used for automatic email/password sign-in.
 */
data class LocalCredentials(
  val email: String,
  val password: String,
)

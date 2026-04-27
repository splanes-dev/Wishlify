package com.splanes.uoc.wishlify.domain.feature.authentication.model

/**
 * Credentials returned by a social authentication provider before the session
 * is finalized in the application backend.
 */
data class SocialCredentials(
  val token: String,
  val username: String,
  val photoUrl: String?
)

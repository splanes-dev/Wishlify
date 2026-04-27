package com.splanes.uoc.wishlify.data.feature.authentication.model

/** Google credential payload returned by Credential Manager before domain mapping. */
data class GoogleCredentials(
  val token: String,
  val username: String,
  val photoUrl: String?,
)

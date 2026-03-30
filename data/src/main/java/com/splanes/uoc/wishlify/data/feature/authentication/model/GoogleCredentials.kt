package com.splanes.uoc.wishlify.data.feature.authentication.model

data class GoogleCredentials(
  val token: String,
  val username: String,
  val photoUrl: String?,
)
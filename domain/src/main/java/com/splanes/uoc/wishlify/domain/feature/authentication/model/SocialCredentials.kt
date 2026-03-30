package com.splanes.uoc.wishlify.domain.feature.authentication.model

data class SocialCredentials(
  val token: String,
  val username: String,
  val photoUrl: String?
)
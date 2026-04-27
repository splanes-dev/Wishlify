package com.splanes.uoc.wishlify.domain.feature.user.model

/** Input required to change the password of the current user. */
data class UpdatePasswordRequest(
  val current: String,
  val new: String
)

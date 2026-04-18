package com.splanes.uoc.wishlify.domain.feature.user.model

data class UpdatePasswordRequest(
  val current: String,
  val new: String
)

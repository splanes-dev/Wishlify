package com.splanes.uoc.wishlify.data.feature.authentication.model

/** Data-layer representation of the current authenticated email account. */
data class Email(
  val email: String,
  val isSocialAccount: Boolean
)

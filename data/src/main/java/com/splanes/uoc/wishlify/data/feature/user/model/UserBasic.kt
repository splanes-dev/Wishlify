package com.splanes.uoc.wishlify.data.feature.user.model

/** Lightweight in-memory user projection reused across data-layer enrichments. */
data class UserBasic(
  val uid: String,
  val username: String,
  val code: String,
  val photoUrl: String?
)

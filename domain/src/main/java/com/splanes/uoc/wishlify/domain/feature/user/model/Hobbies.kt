package com.splanes.uoc.wishlify.domain.feature.user.model

/** User hobby preferences exposed in profile and gifting flows. */
data class Hobbies(
  val enabled: Boolean,
  val values: List<String>,
)

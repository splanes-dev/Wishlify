package com.splanes.uoc.wishlify.data.feature.authentication.model

import kotlinx.serialization.Serializable

/** Serializable local persistence model for email/password credentials. */
@Serializable
data class StoredCredentials(
  val email: String,
  val password: String
)

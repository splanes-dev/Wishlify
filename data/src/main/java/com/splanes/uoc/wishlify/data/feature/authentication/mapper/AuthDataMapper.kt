package com.splanes.uoc.wishlify.data.feature.authentication.mapper

import com.splanes.uoc.wishlify.data.feature.authentication.model.GoogleCredentials
import com.splanes.uoc.wishlify.data.feature.authentication.model.StoredCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.model.LocalCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SocialCredentials

/** Maps authentication models between data-layer and domain representations. */
class AuthDataMapper {

  /** Builds the persisted credentials model from raw email/password values. */
  fun storedCredentialsOf(email: String, password: String): StoredCredentials =
    StoredCredentials(
      email = email,
      password = password
    )

  /** Maps Google credential data into the domain social credentials model. */
  fun mapSocialCredentials(googleCredentials: GoogleCredentials): SocialCredentials =
    SocialCredentials(
      token = googleCredentials.token,
      username = googleCredentials.username,
      photoUrl = googleCredentials.photoUrl
    )

  /** Maps persisted local credentials into the domain local credentials model. */
  fun mapLocalCredentials(storedCredentials: StoredCredentials): LocalCredentials =
    LocalCredentials(
      email = storedCredentials.email,
      password = storedCredentials.password
    )
}

package com.splanes.uoc.wishlify.data.feature.authentication.mapper

import com.splanes.uoc.wishlify.data.feature.authentication.model.GoogleCredentials
import com.splanes.uoc.wishlify.data.feature.authentication.model.StoredCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SocialCredentials

class AuthDataMapper {

  fun storedCredentialsOf(email: String, password: String): StoredCredentials =
    StoredCredentials(
      email = email,
      password = password
    )

  fun mapSocialCredentials(googleCredentials: GoogleCredentials): SocialCredentials =
    SocialCredentials(
      token = googleCredentials.token,
      username = googleCredentials.username,
      photoUrl = googleCredentials.photoUrl
    )
}
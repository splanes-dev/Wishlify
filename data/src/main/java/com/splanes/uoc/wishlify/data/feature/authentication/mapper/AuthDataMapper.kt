package com.splanes.uoc.wishlify.data.feature.authentication.mapper

import com.splanes.uoc.wishlify.data.feature.authentication.model.StoredCredentials

class AuthDataMapper {

  fun storedCredentialsOf(email: String, password: String): StoredCredentials =
    StoredCredentials(
      email = email,
      password = password
    )
}
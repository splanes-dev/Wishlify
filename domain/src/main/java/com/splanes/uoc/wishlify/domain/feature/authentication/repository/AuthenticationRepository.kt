package com.splanes.uoc.wishlify.domain.feature.authentication.repository

import com.splanes.uoc.wishlify.domain.feature.authentication.model.SocialCredentials

interface AuthenticationRepository {
  suspend fun signUp(email: String, password: String): Result<String>
  suspend fun signUp(token: String): Result<String>
  suspend fun googleSignUp(): Result<SocialCredentials>
  suspend fun storeCredentials(email: String, password: String)
}
package com.splanes.uoc.wishlify.domain.feature.authentication.repository

interface AuthenticationRepository {
  suspend fun signUp(email: String, password: String): Result<String>
  suspend fun storeCredentials(email: String, password: String)
}
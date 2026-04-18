package com.splanes.uoc.wishlify.domain.feature.authentication.repository

import com.splanes.uoc.wishlify.domain.feature.authentication.model.LocalCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SocialCredentials

interface AuthenticationRepository {
  suspend fun signUp(email: String, password: String): Result<String>
  suspend fun googleSignUp(): Result<SocialCredentials>
  suspend fun googleSignIn(): Result<SocialCredentials>
  suspend fun signIn(token: String): Result<String>
  suspend fun signIn(email: String, password: String): Result<Unit>
  suspend fun isSignedIn(): Boolean
  suspend fun storeCredentials(email: String, password: String)
  suspend fun fetchStoredCredentials(): LocalCredentials?
  suspend fun cleanStoredCredentials()
  suspend fun updateEmail(credentials: LocalCredentials?, email: String): Result<Unit>
  suspend fun updatePassword(credentials: LocalCredentials, new: String): Result<Unit>
  suspend fun signOut(): Result<Unit>
}
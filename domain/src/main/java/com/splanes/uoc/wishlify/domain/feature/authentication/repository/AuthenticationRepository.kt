package com.splanes.uoc.wishlify.domain.feature.authentication.repository

import com.splanes.uoc.wishlify.domain.feature.authentication.model.LocalCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SocialCredentials

/**
 * Repository contract for authentication and local credential persistence.
 */
interface AuthenticationRepository {
  /** Creates a new email/password account and returns its user identifier. */
  suspend fun signUp(email: String, password: String): Result<String>

  /** Starts the Google sign-up flow and returns provider credentials on success. */
  suspend fun googleSignUp(): Result<SocialCredentials>

  /** Starts the Google sign-in flow and returns provider credentials on success. */
  suspend fun googleSignIn(): Result<SocialCredentials>

  /** Exchanges a provider token for an application session and returns the user identifier. */
  suspend fun signIn(token: String): Result<String>

  /** Authenticates an existing user with email and password. */
  suspend fun signIn(email: String, password: String): Result<Unit>

  /** Returns whether there is already an active authenticated session. */
  suspend fun isSignedIn(): Boolean

  /** Persists local credentials for future automatic sign-in attempts. */
  suspend fun storeCredentials(email: String, password: String)

  /** Retrieves stored local credentials, or `null` when none are available. */
  suspend fun fetchStoredCredentials(): LocalCredentials?

  /** Deletes any persisted local credentials. */
  suspend fun cleanStoredCredentials()

  /** Updates the email associated with the currently persisted credentials. */
  suspend fun updateEmail(credentials: LocalCredentials?, email: String): Result<Unit>

  /** Updates the password associated with the currently persisted credentials. */
  suspend fun updatePassword(credentials: LocalCredentials, new: String): Result<Unit>

  /** Closes the current authenticated session. */
  suspend fun signOut(): Result<Unit>
}

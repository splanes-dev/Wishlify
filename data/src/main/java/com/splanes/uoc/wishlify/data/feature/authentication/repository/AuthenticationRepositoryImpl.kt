package com.splanes.uoc.wishlify.data.feature.authentication.repository

import com.splanes.uoc.wishlify.data.feature.authentication.datasource.AuthLocalDataSource
import com.splanes.uoc.wishlify.data.feature.authentication.datasource.AuthRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.authentication.datasource.GoogleAuthDataSource
import com.splanes.uoc.wishlify.data.feature.authentication.mapper.AuthDataMapper
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.feature.authentication.model.LocalCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SocialCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository

/**
 * Data-layer implementation of [AuthenticationRepository].
 *
 * It orchestrates Firebase authentication, Google credential acquisition and
 * local credential persistence.
 */
class AuthenticationRepositoryImpl(
  private val remoteDataSource: AuthRemoteDataSource,
  private val localDataSource: AuthLocalDataSource,
  private val googleDataSource: GoogleAuthDataSource,
  private val mapper: AuthDataMapper,
) : AuthenticationRepository {

  /** Creates a new email/password account through Firebase Auth. */
  override suspend fun signUp(email: String, password: String): Result<String> =
    runCatching {
      remoteDataSource.signUp(email, password)
    }

  /** Signs in with a Google ID token through Firebase Auth. */
  override suspend fun signIn(token: String): Result<String> =
    runCatching {
      remoteDataSource.signIn(token)
    }

  /** Starts the Google sign-up credential flow and maps the result to domain. */
  override suspend fun googleSignUp(): Result<SocialCredentials> =
    runCatching {
      googleDataSource.getSignUpCredentials()
    }.map(mapper::mapSocialCredentials)

  /** Starts the Google sign-in credential flow and maps the result to domain. */
  override suspend fun googleSignIn(): Result<SocialCredentials> =
    runCatching {
      googleDataSource.getSignInCredentials()
    }.map(mapper::mapSocialCredentials)

  /** Signs in an existing email/password account through Firebase Auth. */
  override suspend fun signIn(email: String, password: String): Result<Unit> =
    runCatching {
      remoteDataSource.signIn(email, password)
    }

  /** Returns whether there is already an authenticated Firebase session. */
  override suspend fun isSignedIn(): Boolean =
    remoteDataSource.isLogged()

  /** Persists local email/password credentials for future automatic sign-in. */
  override suspend fun storeCredentials(email: String, password: String) {
    val credentials = mapper.storedCredentialsOf(email, password)
    localDataSource.storeCredentials(credentials)
  }

  /** Retrieves stored local credentials and maps them to the domain model. */
  override suspend fun fetchStoredCredentials(): LocalCredentials? =
    localDataSource.fetchStoredCredentials()
      ?.let(mapper::mapLocalCredentials)

  /** Deletes any locally persisted credentials. */
  override suspend fun cleanStoredCredentials() {
    localDataSource.cleanStoredCredentials()
  }

  /**
   * Reauthenticates the current user and starts the Firebase email update flow.
   */
  override suspend fun updateEmail(
    credentials: LocalCredentials?,
    email: String
  ): Result<Unit> =
    runCatching {
      if (credentials != null) {
        remoteDataSource.reauthenticate(credentials.email, credentials.password)
        remoteDataSource.updateEmail(email)
      } else {
        throw GenericError.Unknown()
      }
    }

  /** Reauthenticates the current user and updates the Firebase account password. */
  override suspend fun updatePassword(
    credentials: LocalCredentials,
    new: String
  ): Result<Unit> =
    runCatching {
      remoteDataSource.reauthenticate(credentials.email, credentials.password)
      remoteDataSource.updatePassword(new)
  }

  /** Signs out the current Firebase session. */
  override suspend fun signOut(): Result<Unit> =
    runCatching {
      remoteDataSource.signOut()
    }
}

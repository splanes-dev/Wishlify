package com.splanes.uoc.wishlify.data.feature.authentication.repository

import com.splanes.uoc.wishlify.data.feature.authentication.datasource.AuthLocalDataSource
import com.splanes.uoc.wishlify.data.feature.authentication.datasource.AuthRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.authentication.datasource.GoogleAuthDataSource
import com.splanes.uoc.wishlify.data.feature.authentication.mapper.AuthDataMapper
import com.splanes.uoc.wishlify.domain.feature.authentication.model.LocalCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SocialCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository

class AuthenticationRepositoryImpl(
  private val remoteDataSource: AuthRemoteDataSource,
  private val localDataSource: AuthLocalDataSource,
  private val googleDataSource: GoogleAuthDataSource,
  private val mapper: AuthDataMapper,
) : AuthenticationRepository {

  override suspend fun signUp(email: String, password: String): Result<String> =
    runCatching {
      remoteDataSource.signUp(email, password)
    }

  override suspend fun signIn(token: String): Result<String> =
    runCatching {
      remoteDataSource.signIn(token)
    }

  override suspend fun googleSignUp(): Result<SocialCredentials> =
    runCatching {
      googleDataSource.getSignUpCredentials()
    }.map(mapper::mapSocialCredentials)

  override suspend fun googleSignIn(): Result<SocialCredentials> =
    runCatching {
      googleDataSource.getSignInCredentials()
    }.map(mapper::mapSocialCredentials)

  override suspend fun signIn(email: String, password: String): Result<Unit> =
    runCatching {
      remoteDataSource.signIn(email, password)
    }

  override suspend fun isSignedIn(): Boolean =
    remoteDataSource.isLogged()

  override suspend fun storeCredentials(email: String, password: String) {
    val credentials = mapper.storedCredentialsOf(email, password)
    localDataSource.storeCredentials(credentials)
  }

  override suspend fun fetchStoredCredentials(): LocalCredentials? =
    localDataSource.fetchStoredCredentials()
      ?.let(mapper::mapLocalCredentials)

  override suspend fun cleanStoredCredentials() {
    localDataSource.cleanStoredCredentials()
  }
}
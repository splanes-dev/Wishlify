package com.splanes.uoc.wishlify.data.feature.authentication.repository

import com.splanes.uoc.wishlify.data.feature.authentication.datasource.AuthLocalDataSource
import com.splanes.uoc.wishlify.data.feature.authentication.datasource.AuthRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.authentication.mapper.AuthDataMapper
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository

class AuthenticationRepositoryImpl(
  private val remoteDataSource: AuthRemoteDataSource,
  private val localDataSource: AuthLocalDataSource,
  private val mapper: AuthDataMapper,
) : AuthenticationRepository {

  override suspend fun signUp(email: String, password: String): Result<String> =
    runCatching {
      remoteDataSource.signUp(email, password)
    }

  override suspend fun storeCredentials(email: String, password: String) {
    val credentials = mapper.storedCredentialsOf(email, password)
    localDataSource.storeCredentials(credentials)
  }
}
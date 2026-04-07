package com.splanes.uoc.wishlify.data.feature.user.repository

import com.splanes.uoc.wishlify.data.common.utils.sha256
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class UserRepositoryImpl(
  private val remoteDataSource: UserRemoteDataSource,
  private val mapper: UserDataMapper
) : UserRepository {

  override suspend fun existsUser(uid: String): Result<Boolean> =
    runCatching {
      remoteDataSource.existsById(uid)
    }

  override suspend fun addUser(
    uid: String,
    username: String,
    photoUrl: String?
  ) =
    runCatching {
      val entity = mapper.map(uid, username, photoUrl)
      remoteDataSource.add(entity)
    }

  override suspend fun fetchUserById(uid: String): Result<User.Basic> =
    runCatching {
      val entity = remoteDataSource.fetchUserById(uid) ?: throw GenericError.Unknown()
      entity
        .let(mapper::mapToBasic)
        .let(mapper::map)
    }

  override suspend fun searchUsers(query: String): Result<List<User.Basic>> =
    runCatching {
      coroutineScope {
        val resultsByEmailDeferred = async {
          val hash = query.sha256()
          val entity = remoteDataSource.searchUidByEmail(hash)
          if (entity != null) {
            remoteDataSource.fetchUserById(entity.uid)
          } else {
            null
          }
        }
        val resultsByCodeDeferred = async { remoteDataSource.searchByCode(query) }
        val resultsByEmail = resultsByEmailDeferred.await()
        val resultsByCode = resultsByCodeDeferred.await()
        val results = (resultsByCode + resultsByEmail).filterNotNull()

        results
          .map(mapper::mapToBasic)
          .map(mapper::map)
      }
    }
}
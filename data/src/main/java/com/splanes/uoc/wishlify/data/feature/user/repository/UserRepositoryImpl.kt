package com.splanes.uoc.wishlify.data.feature.user.repository

import com.splanes.uoc.wishlify.data.common.utils.sha256
import com.splanes.uoc.wishlify.data.feature.authentication.datasource.AuthRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class UserRepositoryImpl(
  private val authRemoteDataSource: AuthRemoteDataSource,
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
      remoteDataSource.upsertUser(entity)
    }

  override suspend fun fetchUserById(uid: String): Result<User.Basic> =
    runCatching {
      val entity = remoteDataSource.fetchUserById(uid) ?: throw GenericError.Unknown()
      entity
        .let(mapper::mapToBasic)
        .let(mapper::map)
    }

  override suspend fun fetchBasicProfile(uid: String): Result<User.BasicProfile> =
    runCatching {
      val email = authRemoteDataSource.currentUserEmail() ?: throw GenericError.Unknown()
      val entity = remoteDataSource.fetchUserById(uid) ?: throw GenericError.Unknown()
      mapper.mapToBasicProfile(entity, email)
    }

  override suspend fun fetchHobbiesProfile(uid: String): Result<User.HobbiesProfile> =
    runCatching {
      val entity = remoteDataSource.fetchUserById(uid) ?: throw GenericError.Unknown()
      mapper.mapToHobbiesProfile(entity)
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

  override suspend fun updateProfile(
    request: UpdateProfileRequest,
    imageMedia: ImageMedia?
  ): Result<Unit> =
    runCatching {
      val base = remoteDataSource.fetchUserById(request.user.uid)
        ?: error("No user found for id ${request.user.uid}")

      val updated = mapper.map(
        base,
        request,
        imageMedia
      )

      remoteDataSource.upsertUser(updated)
    }
}
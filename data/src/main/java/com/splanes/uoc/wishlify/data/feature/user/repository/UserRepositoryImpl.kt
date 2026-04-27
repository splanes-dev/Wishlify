package com.splanes.uoc.wishlify.data.feature.user.repository

import com.splanes.uoc.wishlify.data.common.utils.sha256
import com.splanes.uoc.wishlify.data.feature.authentication.datasource.AuthRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserLocalDataSource
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Data-layer implementation of [UserRepository].
 *
 * It composes auth data, remote profile persistence and local token storage to
 * expose the user projections and update flows required by the domain layer.
 */
class UserRepositoryImpl(
  private val authRemoteDataSource: AuthRemoteDataSource,
  private val remoteDataSource: UserRemoteDataSource,
  private val localDataSource: UserLocalDataSource,
  private val mapper: UserDataMapper
) : UserRepository {

  /** Checks whether a persisted user profile exists for the given uid. */
  override suspend fun existsUser(uid: String): Result<Boolean> =
    runCatching {
      remoteDataSource.existsById(uid)
    }

  /**
   * Creates the initial persisted user profile, including the locally stored
   * device token when available.
   */
  override suspend fun addUser(
    uid: String,
    username: String,
    photoUrl: String?
  ) =
    runCatching {
      val token = localDataSource.fetchUserToken()
      val entity = mapper.map(uid, token, username, photoUrl)
      remoteDataSource.upsertUser(entity)
    }

  /** Fetches and maps the basic public user projection. */
  override suspend fun fetchUserById(uid: String): Result<User.Basic> =
    runCatching {
      val entity = remoteDataSource.fetchUserById(uid) ?: throw GenericError.Unknown()
      entity
        .let(mapper::mapToBasic)
        .let(mapper::map)
    }

  /** Fetches the basic profile projection enriched with the current auth email. */
  override suspend fun fetchBasicProfile(uid: String): Result<User.BasicProfile> =
    runCatching {
      val email = authRemoteDataSource.currentUserEmail() ?: throw GenericError.Unknown()
      val entity = remoteDataSource.fetchUserById(uid) ?: throw GenericError.Unknown()
      mapper.mapToBasicProfile(entity, email)
    }

  /** Fetches the hobbies profile projection of the user. */
  override suspend fun fetchHobbiesProfile(uid: String): Result<User.HobbiesProfile> =
    runCatching {
      val entity = remoteDataSource.fetchUserById(uid) ?: throw GenericError.Unknown()
      mapper.mapToHobbiesProfile(entity)
    }

  /** Fetches the notifications profile projection of the user. */
  override suspend fun fetchNotificationsProfile(uid: String): Result<User.NotificationsProfile> =
    runCatching {
      val entity = remoteDataSource.fetchUserById(uid) ?: throw GenericError.Unknown()
      mapper.mapToNotificationsProfile(entity)
    }

  /**
   * Searches users by combining the hashed-email lookup and the public code
   * search, then maps the combined results into basic domain users.
   */
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

  /** Applies a profile update request and persists the resulting user entity. */
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

  /** Stores the latest device token locally until it can be synced remotely. */
  override suspend fun storeUserToken(token: String) {
    localDataSource.storeUserToken(token)
  }

  /** Synchronizes the locally stored device token into the persisted user profile. */
  override suspend fun updateUserToken(uid: String) {
    val token = localDataSource.fetchUserToken()
    if (token != null) {
      val entity = remoteDataSource.fetchUserById(uid) ?: return
      val updated = entity.copy(token = token)
      remoteDataSource.upsertUser(updated)
    }
  }
}

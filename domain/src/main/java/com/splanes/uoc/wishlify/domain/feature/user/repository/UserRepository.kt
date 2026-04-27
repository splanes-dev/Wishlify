package com.splanes.uoc.wishlify.domain.feature.user.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User

/** Repository contract for user profiles, search and device token updates. */
interface UserRepository {
  /** Returns whether a user profile already exists for the given uid. */
  suspend fun existsUser(uid: String): Result<Boolean>
  /** Creates a new user profile. */
  suspend fun addUser(uid: String, username: String, photoUrl: String? = null): Result<Unit>
  /** Retrieves a lightweight user projection by identifier. */
  suspend fun fetchUserById(uid: String): Result<User.Basic>
  /** Retrieves the basic profile of a user. */
  suspend fun fetchBasicProfile(uid: String): Result<User.BasicProfile>
  /** Retrieves the hobbies profile of a user. */
  suspend fun fetchHobbiesProfile(uid: String): Result<User.HobbiesProfile>
  /** Retrieves the notification preferences profile of a user. */
  suspend fun fetchNotificationsProfile(uid: String): Result<User.NotificationsProfile>
  /** Searches users matching the given query. */
  suspend fun searchUsers(query: String): Result<List<User.Basic>>
  /** Persists a profile update, optionally including a resolved image media reference. */
  suspend fun updateProfile(request: UpdateProfileRequest, imageMedia: ImageMedia?): Result<Unit>
  /** Stores the current device token for later synchronization. */
  suspend fun storeUserToken(token: String)
  /** Refreshes the persisted device token for the given user. */
  suspend fun updateUserToken(uid: String)
}

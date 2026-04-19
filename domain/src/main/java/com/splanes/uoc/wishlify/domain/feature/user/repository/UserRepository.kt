package com.splanes.uoc.wishlify.domain.feature.user.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User

interface UserRepository {
  suspend fun existsUser(uid: String): Result<Boolean>
  suspend fun addUser(uid: String, username: String, photoUrl: String? = null): Result<Unit>
  suspend fun fetchUserById(uid: String): Result<User.Basic>
  suspend fun fetchBasicProfile(uid: String): Result<User.BasicProfile>
  suspend fun fetchHobbiesProfile(uid: String): Result<User.HobbiesProfile>
  suspend fun fetchNotificationsProfile(uid: String): Result<User.NotificationsProfile>
  suspend fun searchUsers(query: String): Result<List<User.Basic>>
  suspend fun updateProfile(request: UpdateProfileRequest, imageMedia: ImageMedia?): Result<Unit>
}
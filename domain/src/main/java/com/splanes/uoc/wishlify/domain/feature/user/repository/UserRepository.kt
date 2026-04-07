package com.splanes.uoc.wishlify.domain.feature.user.repository

import com.splanes.uoc.wishlify.domain.feature.user.model.User

interface UserRepository {
  suspend fun existsUser(uid: String): Result<Boolean>
  suspend fun addUser(uid: String, username: String, photoUrl: String? = null): Result<Unit>
  suspend fun fetchUserById(uid: String): Result<User.Basic>
  suspend fun searchUsers(query: String): Result<List<User.Basic>>
}
package com.splanes.uoc.wishlify.domain.feature.user.repository

interface UserRepository {
  suspend fun addUser(uid: String, username: String, photoUrl: String? = null): Result<Unit>
}
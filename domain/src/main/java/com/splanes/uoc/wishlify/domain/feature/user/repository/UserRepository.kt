package com.splanes.uoc.wishlify.domain.feature.user.repository

interface UserRepository {
  suspend fun addUser(uid: String, username: String): Result<Unit>
}
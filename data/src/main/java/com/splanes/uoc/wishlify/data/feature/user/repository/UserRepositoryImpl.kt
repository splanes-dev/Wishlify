package com.splanes.uoc.wishlify.data.feature.user.repository

import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

class UserRepositoryImpl(
  private val remoteDataSource: UserRemoteDataSource,
  private val mapper: UserDataMapper
) : UserRepository {

  override suspend fun addUser(
    uid: String,
    username: String,
    photoUrl: String?
  ) =
    runCatching {
      val dto = mapper.map(uid, username, photoUrl)
      remoteDataSource.add(dto)
    }
}
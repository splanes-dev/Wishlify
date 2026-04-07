package com.splanes.uoc.wishlify.domain.feature.user.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

class FetchUserByIdUseCase(
  private val repository: UserRepository
) : UseCase() {

  suspend operator fun invoke(uid: String) = execute {
    repository.fetchUserById(uid)
  }
}
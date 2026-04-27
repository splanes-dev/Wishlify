package com.splanes.uoc.wishlify.domain.feature.user.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

/** Retrieves a lightweight user projection by identifier. */
class FetchUserByIdUseCase(
  private val repository: UserRepository
) : UseCase() {

  /** Fetches the user identified by [uid]. */
  suspend operator fun invoke(uid: String) = execute {
    repository.fetchUserById(uid)
  }
}

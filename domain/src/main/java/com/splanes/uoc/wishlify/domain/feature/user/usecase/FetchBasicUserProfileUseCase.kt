package com.splanes.uoc.wishlify.domain.feature.user.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

/** Retrieves the basic profile of the current user. */
class FetchBasicUserProfileUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: UserRepository
) : UseCase() {

  /** Fetches the current user's basic profile. */
  suspend operator fun invoke() = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchBasicProfile(uid).getOrThrow()
      }
  }
}

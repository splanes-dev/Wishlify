package com.splanes.uoc.wishlify.domain.feature.user.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

/** Searches users while excluding the current user from the results. */
class SearchUserUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: UserRepository,
) : UseCase() {

  /** Searches users matching [query]. */
  suspend operator fun invoke(query: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        val results = repository.searchUsers(query).getOrThrow()
        results.filter { u -> u.uid != uid }
      }
  }
}

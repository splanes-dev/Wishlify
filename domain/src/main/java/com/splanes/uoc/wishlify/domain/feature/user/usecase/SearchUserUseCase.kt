package com.splanes.uoc.wishlify.domain.feature.user.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

class SearchUserUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: UserRepository,
) : UseCase() {

  suspend operator fun invoke(query: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        val results = repository.searchUsers(query).getOrThrow()
        results.filter { u -> u.uid != uid }
      }
  }
}
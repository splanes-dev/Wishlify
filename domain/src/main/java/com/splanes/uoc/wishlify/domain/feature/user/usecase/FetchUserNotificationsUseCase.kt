package com.splanes.uoc.wishlify.domain.feature.user.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

class FetchUserNotificationsUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: UserRepository
) : UseCase() {

  suspend operator fun invoke() = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchNotificationsProfile(uid).getOrThrow()
      }
  }
}
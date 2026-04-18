package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository

class SignOutUseCase(
  private val repository: AuthenticationRepository
) : UseCase() {

  suspend operator fun invoke() = execute {
    repository.signOut()
      .onSuccess { repository.cleanStoredCredentials() }
  }
}
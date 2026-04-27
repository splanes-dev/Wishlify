package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository

/**
 * Closes the current session and removes any locally stored credentials.
 */
class SignOutUseCase(
  private val repository: AuthenticationRepository
) : UseCase() {

  /**
   * Signs the user out and clears the persisted local credentials on success.
   */
  suspend operator fun invoke() = execute {
    repository.signOut()
      .onSuccess { repository.cleanStoredCredentials() }
  }
}

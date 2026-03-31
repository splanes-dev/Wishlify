package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.error.SignInError
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository

class AutoSignInUseCase(
  private val authRepository: AuthenticationRepository
) : UseCase() {

  suspend operator fun invoke() = execute {
    if (!authRepository.isSignedIn()) {
      val credentials = authRepository.fetchStoredCredentials()
      if (credentials != null) {
        authRepository.signIn(credentials.email, credentials.password)
          .onFailure { error ->
            if (error is SignInError.InvalidEmail || error is SignInError.InvalidCredentials) {
              authRepository.cleanStoredCredentials()
            }
          }
      } else {
        Result.failure(SignInError.AutoSignInFailed())
      }
    } else {
      Result.success(Unit)
    }
  }
}
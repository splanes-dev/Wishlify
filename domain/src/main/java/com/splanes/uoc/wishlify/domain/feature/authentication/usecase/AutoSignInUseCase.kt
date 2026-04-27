package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.error.SignInError
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository

/**
 * Restores the user session with locally stored email/password credentials when possible.
 *
 * If the stored credentials are no longer valid, they are removed to avoid
 * repeating the same failing automatic sign-in attempt.
 */
class AutoSignInUseCase(
  private val authRepository: AuthenticationRepository
) : UseCase() {

  /**
   * Attempts automatic sign-in unless the user already has an active session.
   *
   * @return Successful [Result] when the session is already active or can be restored.
   * Returns [SignInError.AutoSignInFailed] when there are no stored credentials.
   */
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

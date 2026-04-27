package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

/**
 * Registers a new user through Google authentication and creates the
 * corresponding application user profile.
 */
class GoogleSignUpUseCase(
  private val authRepository: AuthenticationRepository,
  private val userRepository: UserRepository
) : UseCase() {

  /**
   * Executes the Google sign-up flow without timeout restrictions.
   */
  suspend operator fun invoke() = execute(timeout = NoTimeout) {
    val socialCredentials = authRepository.googleSignUp()
    socialCredentials
      .mapCatching { credentials ->
        val uid = authRepository.signIn(credentials.token).getOrThrow()
        userRepository.addUser(uid, credentials.username, credentials.photoUrl).getOrThrow()
      }
  }
}

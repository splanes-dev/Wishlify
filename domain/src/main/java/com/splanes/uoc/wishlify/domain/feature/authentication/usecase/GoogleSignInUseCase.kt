package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

/**
 * Signs the user in with Google and ensures the corresponding application user exists.
 *
 * If the authenticated account has no user profile yet, it is created from the
 * provider data returned by Google.
 */
class GoogleSignInUseCase(
  private val authRepository: AuthenticationRepository,
  private val userRepository: UserRepository,
) : UseCase() {

  /**
   * Executes the Google sign-in flow without timeout restrictions.
   */
  suspend operator fun invoke() = execute(NoTimeout) {
    val socialCredentials = authRepository.googleSignIn()
    socialCredentials
      .mapCatching { credentials ->
        val uid = authRepository.signIn(credentials.token).getOrThrow()
        if (!userRepository.existsUser(uid).getOrThrow()) {
          userRepository.addUser(uid, credentials.username, credentials.photoUrl).getOrThrow()
        }
      }
  }
}

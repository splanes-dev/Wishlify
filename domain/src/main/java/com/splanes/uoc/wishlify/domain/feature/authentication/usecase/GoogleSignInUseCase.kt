package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

class GoogleSignInUseCase(
  private val authRepository: AuthenticationRepository,
  private val userRepository: UserRepository,
) : UseCase() {

  suspend operator fun invoke() = execute {
    val socialCredentials = authRepository.googleSignIn()
    socialCredentials
      .mapCatching { credentials ->
        val uid = authRepository.signIn(credentials.token).getOrThrow()
        if (!userRepository.existsUser(uid).getOrThrow()) {
          userRepository.addUser(uid, credentials.username, credentials.photoUrl)
        }
      }
  }
}
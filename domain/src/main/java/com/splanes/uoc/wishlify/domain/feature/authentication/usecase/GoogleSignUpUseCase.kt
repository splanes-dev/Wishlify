package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

class GoogleSignUpUseCase(
  private val authRepository: AuthenticationRepository,
  private val userRepository: UserRepository
) : UseCase() {

  suspend operator fun invoke() = execute(timeout = NoTimeout) {
    val socialCredentials = authRepository.googleSignUp()
    socialCredentials
      .mapCatching { credentials ->
        val uid = authRepository.signUp(credentials.token).getOrThrow()
        userRepository.addUser(uid, credentials.username, credentials.photoUrl)
      }
  }
}
package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SignUpRequest
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

/**
 * Registers a new user with email and password and creates the corresponding
 * application user profile.
 */
class SignUpUseCase(
  private val authRepository: AuthenticationRepository,
  private val userRepository: UserRepository,
) : UseCase() {

  /**
   * Creates the account described by [request] and stores the local credentials
   * when the flow completes successfully.
   */
  suspend operator fun invoke(request: SignUpRequest) = execute {
    authRepository.signUp(request.email, request.password)
      .mapCatching { uid ->
        userRepository.addUser(uid, request.username).getOrThrow()
      }
      .onSuccess {
        authRepository.storeCredentials(request.email, request.password)
      }
  }
}

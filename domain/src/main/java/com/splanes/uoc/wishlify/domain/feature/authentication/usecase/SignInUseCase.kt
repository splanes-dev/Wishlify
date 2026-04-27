package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SignInRequest
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

/**
 * Authenticates an existing user with email and password.
 *
 * On success it also persists the credentials locally and requests a refresh
 * of the user's push notification token.
 */
class SignInUseCase(
  private val authRepository: AuthenticationRepository,
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val userRepository: UserRepository,
) : UseCase() {

  /**
   * Signs the user in with the provided [request].
   */
  suspend operator fun invoke(request: SignInRequest) = execute {
    authRepository.signIn(request.email, request.password)
      .onSuccess {
        getCurrentUserIdUseCase().map { uid -> userRepository.updateUserToken(uid) }
        authRepository.storeCredentials(request.email, request.password)
      }
  }
}

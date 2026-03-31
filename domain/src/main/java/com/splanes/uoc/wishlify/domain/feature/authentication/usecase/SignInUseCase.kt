package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SignInRequest
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository

class SignInUseCase(
  private val authRepository: AuthenticationRepository
) : UseCase() {

  suspend operator fun invoke(request: SignInRequest) = execute {
    authRepository.signIn(request.email, request.password)
      .onSuccess {
        authRepository.storeCredentials(request.email, request.password)
      }
  }
}
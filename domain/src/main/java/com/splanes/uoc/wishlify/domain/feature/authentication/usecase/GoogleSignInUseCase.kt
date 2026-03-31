package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository

class GoogleSignInUseCase(
  private val authRepository: AuthenticationRepository
) : UseCase() {

  suspend operator fun invoke() = execute {
    authRepository.googleSignIn()
  }
}
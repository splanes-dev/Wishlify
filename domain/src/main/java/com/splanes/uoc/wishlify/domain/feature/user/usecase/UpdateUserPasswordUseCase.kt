package com.splanes.uoc.wishlify.domain.feature.user.usecase

import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdatePasswordRequest

class UpdateUserPasswordUseCase(
  val authenticationRepository: AuthenticationRepository
) : UseCase() {

  suspend operator fun invoke(request: UpdatePasswordRequest) = execute {
    val localCredentials =
      authenticationRepository.fetchStoredCredentials() ?: throw GenericError.Unknown()

    if (localCredentials.password != request.current) {
      error("Password do not match")
    }

    authenticationRepository.updatePassword(
      credentials = localCredentials,
      new = request.new
    ).onSuccess {
      authenticationRepository.signOut()
      authenticationRepository.cleanStoredCredentials()
    }
  }
}
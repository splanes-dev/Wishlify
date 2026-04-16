package com.splanes.uoc.wishlify.domain.feature.secresanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

class UnshareWishlistSecretSantaUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SecretSantaRepository
) : UseCase() {

  suspend operator fun invoke(eventId: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.unshareWishlistToGiver(uid, eventId)
      }
  }
}
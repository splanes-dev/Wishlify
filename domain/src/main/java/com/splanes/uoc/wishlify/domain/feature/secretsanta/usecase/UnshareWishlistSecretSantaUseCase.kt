package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/** Removes the current user's shared wishlist from a Secret Santa event. */
class UnshareWishlistSecretSantaUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SecretSantaRepository
) : UseCase() {

  /** Removes the current user's shared wishlist from [eventId]. */
  suspend operator fun invoke(eventId: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.unshareWishlistToGiver(uid, eventId).getOrThrow()
      }
  }
}

package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/** Shares one of the current user's wishlists with their assigned giver. */
class ShareWishlistSecretSantaUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SecretSantaRepository,
) : UseCase() {

  /** Shares [wishlistId] inside the Secret Santa event identified by [eventId]. */
  suspend operator fun invoke(eventId: String, wishlistId: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.shareWishlistToGiver(uid, eventId, wishlistId).getOrThrow()
      }
  }
}

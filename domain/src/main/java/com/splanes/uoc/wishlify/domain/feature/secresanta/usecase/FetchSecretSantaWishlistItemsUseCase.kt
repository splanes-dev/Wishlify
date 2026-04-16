package com.splanes.uoc.wishlify.domain.feature.secresanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

class FetchSecretSantaWishlistItemsUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SecretSantaRepository,
) : UseCase() {

  suspend operator fun invoke(
    eventId: String,
    wishlistOwnerId: String?,
    isOwnWishlist: Boolean
  ) = execute {
    runCatching {
      val uid = when {
        isOwnWishlist -> getCurrentUserIdUseCase().getOrThrow()
        else -> wishlistOwnerId
      }

      if (uid == null) {
        error("Impossible to fetch secret santa wishlist item, no uid specified")
      }

      repository.fetchSecretSantaWishlistItems(eventId, uid).getOrThrow()
    }
  }
}
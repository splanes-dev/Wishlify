package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/** Retrieves the items of a wishlist shared within a Secret Santa event. */
class FetchSecretSantaWishlistItemsUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SecretSantaRepository,
) : UseCase() {

  /**
   * Fetches wishlist items either for the current user or for the explicitly
   * provided [wishlistOwnerId], depending on [isOwnWishlist].
   */
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

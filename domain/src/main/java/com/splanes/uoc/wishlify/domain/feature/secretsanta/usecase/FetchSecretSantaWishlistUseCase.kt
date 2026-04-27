package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/** Retrieves the wishlist shared within a Secret Santa event. */
class FetchSecretSantaWishlistUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SecretSantaRepository,
) : UseCase() {

  /**
   * Fetches the wishlist either for the current user or for the explicitly
   * provided [wishlistOwnerId], depending on [isOwnWishlist].
   */
  suspend operator fun invoke(eventId: String, wishlistOwnerId: String?, isOwnWishlist: Boolean) =
    execute {
      runCatching {
        val uid = when {
          isOwnWishlist -> getCurrentUserIdUseCase().getOrThrow()
          else -> wishlistOwnerId
        }

        if (uid == null) {
          error("Impossible to fetch secret santa wishlist item, no uid specified")
        }

        repository.fetchSecretSantaWishlist(eventId, uid).getOrThrow()
      }
    }
}

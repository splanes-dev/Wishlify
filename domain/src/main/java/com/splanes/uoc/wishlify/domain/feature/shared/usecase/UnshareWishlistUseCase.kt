package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

/** Stops sharing a wishlist and converts it back to a private flow. */
class UnshareWishlistUseCase(
  private val repository: SharedWishlistsRepository,
) : UseCase() {

  /** Unshares the wishlist identified by [wishlistId]. */
  suspend operator fun invoke(wishlistId: String) = execute {
    repository.unshareSharedWishlist(wishlistId)
  }
}

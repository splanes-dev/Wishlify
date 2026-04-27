package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/** Retrieves all items of a wishlist. */
class FetchWishlistItemsUseCase(
  private val wishlistsRepository: WishlistsRepository
) : UseCase() {

  /** Fetches the items of the wishlist identified by [wishlistId]. */
  suspend operator fun invoke(wishlistId: String) = execute {
    wishlistsRepository.fetchWishlistItems(wishlistId)
  }
}

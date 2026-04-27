package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/** Retrieves a single item from a wishlist. */
class FetchWishlistItemUseCase(
  private val wishlistsRepository: WishlistsRepository
) : UseCase() {

  /** Fetches the item identified by [item] from [wishlistId]. */
  suspend operator fun invoke(wishlistId: String, item: String) = execute {
    wishlistsRepository.fetchWishlistItem(wishlistId, item)
  }
}

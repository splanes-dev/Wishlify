package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/**
 * Deletes an item from a wishlist and removes its stored image when the deletion succeeds.
 */
class DeleteWishlistItemUseCase(
  private val mediaRepository: ImageMediaRepository,
  private val repository: WishlistsRepository
) : UseCase() {

  /** Deletes the item identified by [item] from the wishlist [wishlist]. */
  suspend operator fun invoke(wishlist: String, item: String) = execute {
    repository.deleteWishlistItem(wishlist, item)
      .onSuccess {
        val path = ImageMediaPath.WishlistItem(
          wishlistId = wishlist,
          itemId = item
        )
        mediaRepository.delete(path)
      }
  }
}

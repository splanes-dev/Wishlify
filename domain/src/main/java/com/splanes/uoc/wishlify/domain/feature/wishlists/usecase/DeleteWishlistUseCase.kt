package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

class DeleteWishlistUseCase(
  private val mediaRepository: ImageMediaRepository,
  private val repository: WishlistsRepository,
) : UseCase() {

  suspend operator fun invoke(wishlist: Wishlist) = execute {
    mediaRepository.delete(ImageMediaPath.WishlistCover(wishlist.id))
    repository.deleteWishlist(wishlist.id)
  }
}
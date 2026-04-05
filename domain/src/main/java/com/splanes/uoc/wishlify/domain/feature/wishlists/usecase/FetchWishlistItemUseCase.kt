package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

class FetchWishlistItemUseCase(
  private val wishlistsRepository: WishlistsRepository
) : UseCase() {

  suspend operator fun invoke(wishlistId: String, item: String) = execute {
    wishlistsRepository.fetchWishlistItem(wishlistId, item)
  }
}
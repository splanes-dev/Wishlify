package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

class FetchWishlistUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val wishlistsRepository: WishlistsRepository
) : UseCase() {

  suspend operator fun invoke(wishlistId: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        wishlistsRepository.fetchWishlist(uid, wishlistId).getOrThrow()
      }
  }
}
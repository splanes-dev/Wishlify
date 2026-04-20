package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

class UnshareWishlistUseCase(
  private val repository: SharedWishlistsRepository,
) : UseCase() {

  suspend operator fun invoke(wishlistId: String) = execute {
    repository.unshareSharedWishlist(wishlistId)
  }
}
package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

class UnshareWishlistUseCase(
  private val repository: SharedWishlistsRepository,
) : UseCase() {

  suspend operator fun invoke(sharedWishlist: SharedWishlist) = execute {
    repository.unshareSharedWishlist(
      sharedWishlistId = sharedWishlist.id,
      linkedWishlistId = sharedWishlist.linkedWishlist.id
    )
  }
}
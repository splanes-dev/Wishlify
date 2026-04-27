package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

/** Retrieves the items of a shared wishlist for the current user. */
class FetchSharedWishlistItemsUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository
) : UseCase() {

  /** Fetches the items of the shared wishlist identified by [sharedWishlistId]. */
  suspend operator fun invoke(sharedWishlistId: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchSharedWishlistItems(uid, sharedWishlistId).getOrThrow()
      }
  }
}

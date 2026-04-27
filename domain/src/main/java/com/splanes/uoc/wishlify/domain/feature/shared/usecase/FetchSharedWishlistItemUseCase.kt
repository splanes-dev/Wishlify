package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

/** Retrieves a single item of a shared wishlist for the current user. */
class FetchSharedWishlistItemUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository,
) : UseCase() {

  /** Fetches the shared wishlist item identified by [sharedWishlistItemId]. */
  suspend operator fun invoke(
    sharedWishlistId: String,
    sharedWishlistItemId: String
  ) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchSharedWishlistItem(uid, sharedWishlistId, sharedWishlistItemId).getOrThrow()
      }
  }
}

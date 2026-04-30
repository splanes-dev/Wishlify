package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

/** Subscribes to real-time updates of shared wishlist items. */
class SubscribeSharedWishlistItemsUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository
) : UseCase() {

  /** Returns a live stream of items for the shared wishlist identified by [sharedWishlistId]. */
  suspend operator fun invoke(sharedWishlistId: String) =
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.subscribeToSharedWishlistItems(uid, sharedWishlistId)
      }
}

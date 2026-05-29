package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

/** Subscribes to real-time updates of the shared wishlists visible to the current user. */
class SubscribeSharedWishlistsUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository
) : UseCase() {

  /** Returns a live stream of the current user's shared wishlists. */
  suspend operator fun invoke() =
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.subscribeToSharedWishlists(uid)
      }
}

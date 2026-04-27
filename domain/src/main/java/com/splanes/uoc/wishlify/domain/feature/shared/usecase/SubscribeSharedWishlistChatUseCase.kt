package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

/** Subscribes to real-time chat messages of a shared wishlist. */
class SubscribeSharedWishlistChatUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository,
) : UseCase() {

  /** Returns a live stream of chat messages for [wishlistId]. */
  operator fun invoke(wishlistId: String, limit: Int = 30) =
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.subscribeToWishlistsChatMessages(uid, wishlistId, limit)
      }
}

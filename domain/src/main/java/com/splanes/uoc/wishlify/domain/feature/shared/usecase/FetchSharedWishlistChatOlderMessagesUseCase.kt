package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

/** Fetches a paginated batch of older chat messages for a shared wishlist. */
class FetchSharedWishlistChatOlderMessagesUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository,
) : UseCase() {

  /** Fetches older messages for [wishlistId] starting from the given [cursor]. */
  suspend operator fun invoke(wishlistId: String, cursor: Long) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchSharedWishlistMessages(uid, wishlistId, cursor, limit = 30).getOrThrow()
      }
  }
}

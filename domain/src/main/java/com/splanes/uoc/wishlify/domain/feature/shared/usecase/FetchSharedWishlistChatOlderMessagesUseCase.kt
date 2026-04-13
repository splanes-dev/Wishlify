package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

class FetchSharedWishlistChatOlderMessagesUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository,
) : UseCase() {

  suspend operator fun invoke(wishlistId: String, cursor: Long) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchSharedWishlistMessages(uid, wishlistId, cursor, limit = 30).getOrThrow()
      }
  }
}
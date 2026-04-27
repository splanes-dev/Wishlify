package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemUpdateStateRequest
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

/** Applies a collaborative state transition to a shared wishlist item. */
class UpdateSharedWishlistItemUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository,
) : UseCase() {

  /** Updates the state of the shared item described by [request]. */
  suspend operator fun invoke(request: SharedWishlistItemUpdateStateRequest) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.updateSharedWishlistItemState(uid, request).getOrThrow()
      }
  }
}

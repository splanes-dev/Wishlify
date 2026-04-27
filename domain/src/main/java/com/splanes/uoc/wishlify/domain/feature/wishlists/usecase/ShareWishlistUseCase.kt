package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.ShareWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/** Shares a private wishlist with other participants. */
class ShareWishlistUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: WishlistsRepository,
) : UseCase() {

  /** Shares the wishlist according to [request]. */
  suspend operator fun invoke(request: ShareWishlistRequest) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.shareWishlist(uid, request).getOrThrow()
      }
  }
}

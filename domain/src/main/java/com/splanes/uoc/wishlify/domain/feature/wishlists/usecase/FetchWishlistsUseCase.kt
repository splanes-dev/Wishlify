package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/** Retrieves the wishlists visible to the current user. */
class FetchWishlistsUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: WishlistsRepository
) : UseCase() {

  /** Fetches the current user's wishlists. */
  suspend operator fun invoke() = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchWishlists(uid).getOrThrow()
      }
  }
}

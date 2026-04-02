package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistType
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

class FetchWishlistsUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: WishlistsRepository
) : UseCase() {

  suspend operator fun invoke(type: WishlistType = WishlistType.All) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchWishlists(uid).getOrThrow()
      }
  }
}
package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistType
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

class FetchSharedWishlistsUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository
) : UseCase() {

  suspend operator fun invoke(type: SharedWishlistType) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchSharedWishlists(uid).getOrThrow()
      }
  }
}
package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemUpdateStateRequest
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

class UpdateSharedWishlistItemUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository,
) : UseCase() {

  suspend operator fun invoke(request: SharedWishlistItemUpdateStateRequest) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.updateSharedWishlistItemState(uid, request).getOrThrow()
      }
  }
}
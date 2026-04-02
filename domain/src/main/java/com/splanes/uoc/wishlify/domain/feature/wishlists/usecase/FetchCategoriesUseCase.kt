package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

class FetchCategoriesUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: WishlistsRepository
) : UseCase() {

  suspend operator fun invoke() = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchCategories(uid).getOrThrow()
      }
  }
}
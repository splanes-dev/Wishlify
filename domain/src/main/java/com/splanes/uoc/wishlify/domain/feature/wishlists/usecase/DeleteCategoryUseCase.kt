package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/** Deletes a wishlist category owned by the current user. */
class DeleteCategoryUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: WishlistsRepository,
) : UseCase() {

  /** Deletes the category identified by [categoryId]. */
  suspend operator fun invoke(categoryId: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.deleteCategory(uid, categoryId)
      }
  }
}

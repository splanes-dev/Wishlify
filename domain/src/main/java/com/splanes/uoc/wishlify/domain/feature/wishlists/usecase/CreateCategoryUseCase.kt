package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.common.utils.newUuid
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/** Creates a new wishlist category for the current user. */
class CreateCategoryUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: WishlistsRepository,
) : UseCase() {

  /** Creates a category with the given [name] and [color]. */
  suspend operator fun invoke(name: String, color: Category.CategoryColor) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        val category = Category(
          id = newUuid(),
          name = name,
          color = color
        )

        repository.addCategory(uid, category).getOrThrow()

        category
      }
  }
}

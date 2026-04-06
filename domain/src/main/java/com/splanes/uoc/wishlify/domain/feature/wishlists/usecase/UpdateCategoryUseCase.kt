package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

class UpdateCategoryUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: WishlistsRepository,
) : UseCase() {

  suspend operator fun invoke(categoryId: String, name: String, color: Category.CategoryColor) =
    execute {
      getCurrentUserIdUseCase()
        .mapCatching { uid ->
          val category = Category(
            id = categoryId,
            name = name,
            color = color
          )

          repository.updateCategory(uid, category).getOrThrow()

          category
        }
    }
}
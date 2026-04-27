package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories.model

import com.splanes.uoc.wishlify.presentation.feature.wishlists.model.CategoryUiModel

/**
 * Actions available for wishlist category administration.
 */
sealed interface CategoryAction {
  data object New : CategoryAction
  data class Edit(val category: CategoryUiModel) : CategoryAction
  data class Delete(val category: CategoryUiModel) : CategoryAction
}

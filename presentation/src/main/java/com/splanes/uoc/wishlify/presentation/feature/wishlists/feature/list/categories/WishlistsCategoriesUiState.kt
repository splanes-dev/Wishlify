package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.model.CategoryUiModel

sealed interface WishlistsCategoriesUiState {

  data object Loading : WishlistsCategoriesUiState
  data class Empty(
    val error: ErrorUiModel?,
  ) : WishlistsCategoriesUiState

  data class Categories(
    val categories: List<CategoryUiModel>,
    val isConfirmDeleteCategoryDialogVisible: Boolean,
    val isCategoryModalVisible: Boolean,
    val categoryNameInputError: String?,
    val selectedCategory: Category?,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ) : WishlistsCategoriesUiState
}

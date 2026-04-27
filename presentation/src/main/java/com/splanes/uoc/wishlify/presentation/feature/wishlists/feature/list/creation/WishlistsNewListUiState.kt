package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation

import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.model.CategoryUiModel

/**
 * UI state for the wishlist creation flow.
 */
data class WishlistsNewListUiState(
  val categories: List<CategoryUiModel>,
  val isOwnWishlist: Boolean,
  val editorLink: String,
  val nameError: String?,
  val targetError: String?,
  val descriptionError: String?,
  val newCategoryNameError: String?,
  val isNewCategoryModalOpen: Boolean,
  val isLoading: Boolean,
  val error: ErrorUiModel?,
)

/**
 * One-off effects emitted by the wishlist creation flow.
 */
sealed interface WishlistsNewListUiSideEffect {
  data object WishlistCreated : WishlistsNewListUiSideEffect
}

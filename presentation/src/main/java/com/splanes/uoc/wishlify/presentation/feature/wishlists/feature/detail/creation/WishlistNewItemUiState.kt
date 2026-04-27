package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.creation

import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemForm
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemUiFormErrors

/**
 * UI state for the wishlist item creation flow.
 */
data class WishlistNewItemUiState(
  val form: WishlistItemForm,
  val formErrors: WishlistItemUiFormErrors,
  val isLoading: Boolean,
  val error: ErrorUiModel?
)

/**
 * One-off effects emitted by the wishlist item creation flow.
 */
sealed interface WishlistNewItemUiSideEffect {
  data object ItemCreated : WishlistNewItemUiSideEffect
}

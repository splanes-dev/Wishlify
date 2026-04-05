package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.creation

import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemForm
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemUiFormErrors

data class WishlistNewItemUiState(
  val form: WishlistItemForm,
  val formErrors: WishlistItemUiFormErrors,
  val isLoading: Boolean,
  val error: ErrorUiModel?
)

sealed interface WishlistNewItemUiSideEffect {
  data object ItemCreated : WishlistNewItemUiSideEffect
}
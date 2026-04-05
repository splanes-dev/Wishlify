package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.edition

import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemForm
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemUiFormErrors

sealed interface WishlistEditItemUiState {

  data object Loading : WishlistEditItemUiState

  data object Error : WishlistEditItemUiState

  data class Form(
    val form: WishlistItemForm,
    val formErrors: WishlistItemUiFormErrors,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : WishlistEditItemUiState
}

sealed interface WishlistEditItemUiSideEffect {
  data object ItemEdited : WishlistEditItemUiSideEffect
}
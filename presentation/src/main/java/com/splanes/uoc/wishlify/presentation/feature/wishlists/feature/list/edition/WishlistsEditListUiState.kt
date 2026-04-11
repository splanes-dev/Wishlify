package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.edition

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.model.CategoryUiModel

sealed interface WishlistsEditListUiState {

  data object Loading : WishlistsEditListUiState

  data object Error : WishlistsEditListUiState

  data class Form(
    val wishlist: Wishlist,
    val categories: List<CategoryUiModel>,
    val nameError: String?,
    val targetError: String?,
    val descriptionError: String?,
    val newCategoryNameError: String?,
    val isNewCategoryModalOpen: Boolean,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ) : WishlistsEditListUiState
}

sealed interface WishlistsEditListUiSideEffect {
  data object WishlistUpdated : WishlistsEditListUiSideEffect
}
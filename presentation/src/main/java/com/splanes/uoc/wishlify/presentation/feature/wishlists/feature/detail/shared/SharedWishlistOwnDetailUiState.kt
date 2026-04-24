package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.shared

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.common.components.filters.FilterProduct
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

sealed interface SharedWishlistOwnDetailUiState {
  data class Error(
    val wishlistName: String,
    val wishlistTarget: String?
  ) : SharedWishlistOwnDetailUiState

  data class Loading(
    val wishlistName: String,
    val wishlistTarget: String?
  ) : SharedWishlistOwnDetailUiState

  data class Listing(
    val wishlistName: String,
    val wishlistTarget: String?,
    val wishlist: Wishlist.Shared,
    val itemSelected: WishlistItem?,
    val isItemDetailModalOpen: Boolean,
    val items: List<WishlistItem>,
    val productFilters: List<FilterProduct>,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : SharedWishlistOwnDetailUiState
}

sealed interface SharedWishlistOwnDetailUiSideEffect {
  data object WishlistUnshared : SharedWishlistOwnDetailUiSideEffect
}
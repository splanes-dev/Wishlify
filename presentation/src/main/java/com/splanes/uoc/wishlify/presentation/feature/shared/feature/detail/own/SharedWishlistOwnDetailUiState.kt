package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own

import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

sealed interface SharedWishlistOwnDetailUiState {
  data class Error(
    val wishlistName: String,
    val wishlistTarget: String
  ) : SharedWishlistOwnDetailUiState

  data class Loading(
    val wishlistName: String,
    val wishlistTarget: String
  ) : SharedWishlistOwnDetailUiState

  data class Listing(
    val wishlistName: String,
    val wishlistTarget: String,
    val wishlist: SharedWishlist,
    val itemSelected: SharedWishlistItem?,
    val isItemDetailModalOpen: Boolean,
    val items: List<SharedWishlistItem>,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : SharedWishlistOwnDetailUiState
}

sealed interface SharedWishlistOwnDetailUiSideEffect {
  data object WishlistUnshared : SharedWishlistOwnDetailUiSideEffect
}
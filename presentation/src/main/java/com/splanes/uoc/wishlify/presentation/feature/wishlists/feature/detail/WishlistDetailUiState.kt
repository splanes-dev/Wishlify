package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.common.components.filters.FilterProduct
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/**
 * UI state for the wishlist detail screen.
 */
sealed interface WishlistDetailUiState {

  data class Error(
    val wishlistName: String,
  ) : WishlistDetailUiState

  data class Loading(
    val wishlistName: String,
  ) : WishlistDetailUiState

  data class Empty(
    val wishlistName: String,
    val wishlist: Wishlist,
    val productFilters: List<FilterProduct>,
    val isNewItemByLinkModalOpen: Boolean,
    val newItemByLinkError: String?,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : WishlistDetailUiState

  data class Listing(
    val wishlistName: String,
    val wishlist: Wishlist,
    val isItemDetailModalOpen: Boolean,
    val isItemDetailButtonLoading: Boolean,
    val itemSelected: WishlistItem?,
    val isNewItemByLinkModalOpen: Boolean,
    val newItemByLinkError: String?,
    val items: List<WishlistItem>,
    val productFilters: List<FilterProduct>,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : WishlistDetailUiState {

    /**
     * Indicates whether the current wishlist can still be shared based on its remaining items.
     */
    fun isShareable() = items.any { item -> item.purchased == null }
  }
}

/**
 * One-off effects emitted from the wishlist detail screen.
 */
sealed interface WishlistDetailUiSideEffect {
  data object WishlistDeleted : WishlistDetailUiSideEffect
  data class NavToEdit(val itemId: String) : WishlistDetailUiSideEffect
}

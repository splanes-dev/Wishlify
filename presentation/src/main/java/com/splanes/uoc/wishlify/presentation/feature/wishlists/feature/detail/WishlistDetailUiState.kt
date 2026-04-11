package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

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
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : WishlistDetailUiState {

    fun isShareable() = items.any { item -> item.purchased == null }
  }
}


sealed interface WishlistDetailUiSideEffect {
  data class NavToEdit(val itemId: String) : WishlistDetailUiSideEffect
}
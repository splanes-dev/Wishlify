package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsTab

sealed interface WishlistsListUiState {
  data class Empty(
    val tabSelected:  WishlistsTab,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ): WishlistsListUiState

  data class Listing(
    val tabSelected:  WishlistsTab,
    val wishlistsOwn: List<Wishlist.Own>,
    val wishlistsThirdParty: List<Wishlist.ThirdParty>,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ): WishlistsListUiState
}
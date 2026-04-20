package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsFilter

sealed interface WishlistsListUiState {

  data object Loading : WishlistsListUiState

  data class Empty(
    val filtersState: WishlistsFiltersState,
    val categories: List<Category>,
    val sharedWishlistFeedback: String?,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ): WishlistsListUiState

  data class Listing(
    val wishlists: List<Wishlist>,
    val filtersState: WishlistsFiltersState,
    val categories: List<Category>,
    val sharedWishlistFeedback: String?,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ): WishlistsListUiState
}

data class WishlistsFiltersState(
  val target: WishlistsFilter.Target? = null,
  val category: WishlistsFilter.Category? = null,
  val shareStatus: WishlistsFilter.ShareStatus? = null,
  val availability: WishlistsFilter.Availability? = null
) {
  fun hasFilters() =
    target != null || category != null && shareStatus != null || availability != null
}
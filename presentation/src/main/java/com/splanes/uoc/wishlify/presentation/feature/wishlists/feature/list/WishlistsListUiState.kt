package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

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
    val wishlists: List<Any>,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ): WishlistsListUiState
}
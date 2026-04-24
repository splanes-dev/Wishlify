package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list

import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

sealed interface SharedWishlistsListUiState {

  data object Loading : SharedWishlistsListUiState

  data object Empty : SharedWishlistsListUiState

  data class Listing(
    val wishlists: List<SharedWishlist>,
    val isPermissionModalVisible: Boolean,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ): SharedWishlistsListUiState
}
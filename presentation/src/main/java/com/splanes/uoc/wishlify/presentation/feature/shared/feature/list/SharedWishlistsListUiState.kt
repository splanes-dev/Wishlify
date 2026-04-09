package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list

import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.model.SharedWishlistsTab

sealed interface SharedWishlistsListUiState {

  data class Loading(
    val tabSelected: SharedWishlistsTab,
  ) : SharedWishlistsListUiState

  data class Empty(
    val tabSelected:  SharedWishlistsTab,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ): SharedWishlistsListUiState

  data class Listing(
    val tabSelected:  SharedWishlistsTab,
    val wishlistsOwn: List<SharedWishlist.Own>,
    val wishlistsThirdParty: List<SharedWishlist.ThirdParty>,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ): SharedWishlistsListUiState
}
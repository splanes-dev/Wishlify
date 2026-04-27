package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share

import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/**
 * UI state for the wishlist sharing flow.
 */
sealed interface WishlistShareUiState {
  data object Loading : WishlistShareUiState

  data object Error : WishlistShareUiState

  data class Share(
    val wishlist: Wishlist,
    val groups: List<Group.Basic>,
    val shareLink: String,
    val inputDateError: String?,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ) : WishlistShareUiState
}

/**
 * One-off effects emitted by the wishlist sharing flow.
 */
sealed interface WishlistShareUiSideEffect {
  data class WishlistShared(val wishlistName: String) : WishlistShareUiSideEffect
}

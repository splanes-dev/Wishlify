package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.wishlist

import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaWishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/**
 * UI state for the Secret Santa wishlist detail flow.
 */
sealed interface SecretSantaWishlistUiState {
  data class Loading(val wishlistName: String?) : SecretSantaWishlistUiState
  data class Error(val wishlistName: String?) : SecretSantaWishlistUiState
  data class Listing(
    val wishlist: SecretSantaWishlist,
    val isOwnWishlist: Boolean,
    val items: List<WishlistItem>,
    val itemSelected: WishlistItem?,
    val isItemDetailOpened: Boolean,
    val isLoading: Boolean,
    val error: ErrorUiModel?,
  ) : SecretSantaWishlistUiState
}

/**
 * One-off effects emitted by the Secret Santa wishlist detail flow.
 */
sealed interface SecretSantaWishlistUiSideEffect {
  data object WishlistRemoved : SecretSantaWishlistUiSideEffect
}

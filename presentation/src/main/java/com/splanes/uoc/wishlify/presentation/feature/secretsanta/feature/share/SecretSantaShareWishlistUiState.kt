package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.share

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/**
 * UI state for the flow that shares one of the current user's wishlists with a Secret Santa
 * event.
 */
sealed interface SecretSantaShareWishlistUiState {
  data class Loading(val wishlist: Wishlist.Own?) : SecretSantaShareWishlistUiState {
    val hasBack get() = wishlist != null
  }

  data class Empty(val wishlist: Wishlist.Own?) : SecretSantaShareWishlistUiState {
    val hasBack get() = wishlist != null
  }

  data class Wishlists(
    val wishlists: List<Wishlist.Own>,
    val wishlistSelected: Wishlist.Own?,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : SecretSantaShareWishlistUiState

  data class WishlistDetail(
    val wishlist: Wishlist.Own,
    val items: List<WishlistItem>,
    val isDetailModalOpen: Boolean,
    val itemSelected: WishlistItem?,
  ) : SecretSantaShareWishlistUiState
}

/**
 * One-off effects emitted by the Secret Santa wishlist sharing flow.
 */
sealed interface SecretSantaShareWishlistUiSideEffect {
  data object WishlistShared : SecretSantaShareWishlistUiSideEffect
}

package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail

import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.presentation.common.components.filters.FilterProduct
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.shared.model.SharedWishlistItemStateAction

/**
 * UI state for the third-party shared wishlist detail flow.
 */
sealed interface SharedWishlistThirdPartyDetailUiState {
  data class Error(
    val wishlistName: String,
    val target: String,
  ) : SharedWishlistThirdPartyDetailUiState

  data class Loading(
    val wishlistName: String,
    val target: String,
  ) : SharedWishlistThirdPartyDetailUiState

  data class Listing(
    val wishlistName: String,
    val target: String,
    val wishlist: SharedWishlist.ThirdParty,
    val isInfoBannerVisible: Boolean,
    val isItemDetailModalOpen: Boolean,
    val isWishlistItemStateModalOpen: Boolean,
    val isItemDetailButtonLoading: Boolean,
    val itemSelected: SharedWishlistItem?,
    val itemSelectedToUpdateState: SharedWishlistItem?,
    val itemStateActions: List<SharedWishlistItemStateAction>,
    val items: List<SharedWishlistItem>,
    val productFilters: List<FilterProduct>,
    val shareRequestError: String?,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ) : SharedWishlistThirdPartyDetailUiState
}

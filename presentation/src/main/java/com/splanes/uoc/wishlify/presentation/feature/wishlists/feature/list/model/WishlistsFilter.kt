package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category as CategoryModel

/**
 * Filter values that can be applied to the wishlists list.
 */
sealed interface WishlistsFilter {
  /**
   * Filters by wishlist ownership.
   */
  sealed interface Target : WishlistsFilter

  /**
   * Filters by wishlist category.
   */
  sealed interface Category : WishlistsFilter

  /**
   * Filters by sharing state.
   */
  sealed interface ShareStatus : WishlistsFilter

  /**
   * Filters by remaining available items.
   */
  sealed interface Availability : WishlistsFilter

  data object TargetUnselected : Target
  data object Own : Target
  data object ThirdParty : Target

  data object CategoryUnselected: Category
  data class Categories(val values: List<CategoryModel>) : Category

  data object ShareStatusUnselected : ShareStatus
  data object NotShared : ShareStatus
  data object OnSharedWishlist : ShareStatus
  data object OnSecretSantaEvent : ShareStatus

  data object AvailabilityUnselected: Availability
  data object ItemsAvailable: Availability
  data object ItemsNotAvailable: Availability
}

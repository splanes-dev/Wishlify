package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category as CategoryModel

sealed interface WishlistsFilter {
  // Main filters
  sealed interface Target : WishlistsFilter
  sealed interface Category : WishlistsFilter
  sealed interface ShareStatus : WishlistsFilter
  sealed interface Availability : WishlistsFilter

  // Options
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
package com.splanes.uoc.wishlify.domain.feature.wishlists.model

sealed interface WishlistFilter

sealed interface WishlistType : WishlistFilter {
  data object Own : WishlistType
  data object ThirdParty : WishlistType
  data object All : WishlistType
}
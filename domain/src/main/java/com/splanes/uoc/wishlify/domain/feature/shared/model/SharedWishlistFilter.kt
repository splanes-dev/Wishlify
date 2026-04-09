package com.splanes.uoc.wishlify.domain.feature.shared.model

sealed interface SharedWishlistFilter

sealed interface SharedWishlistType : SharedWishlistFilter {
  data object Own : SharedWishlistType
  data object ThirdParty : SharedWishlistType
  data object All : SharedWishlistType
}
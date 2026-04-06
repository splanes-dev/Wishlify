package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.model

sealed interface WishlistShareFormError

sealed interface DateWishlistShareFormError : WishlistShareFormError {
  data object Blank: DateWishlistShareFormError
  data object Invalid: DateWishlistShareFormError
}
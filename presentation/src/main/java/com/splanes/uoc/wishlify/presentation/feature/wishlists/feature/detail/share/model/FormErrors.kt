package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.model

/**
 * Marker interface for wishlist share form validation errors.
 */
sealed interface WishlistShareFormError

/**
 * Validation errors for the wishlist share deadline field.
 */
sealed interface DateWishlistShareFormError : WishlistShareFormError {
  data object Blank: DateWishlistShareFormError
  data object Invalid: DateWishlistShareFormError
}

package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model

/**
 * Marker interface for wishlist form validation errors.
 */
sealed interface WishlistFormError

/**
 * Validation errors for the wishlist name field.
 */
sealed interface NameWishlistFormError : WishlistFormError {
  data object Length : NameWishlistFormError
}

/**
 * Validation errors for the wishlist target field.
 */
sealed interface TargetWishlistFormError : WishlistFormError {
  data object Blank : TargetWishlistFormError
}

/**
 * Validation errors for the wishlist description field.
 */
sealed interface DescriptionWishlistFormError : WishlistFormError {
  data object Length : DescriptionWishlistFormError
}

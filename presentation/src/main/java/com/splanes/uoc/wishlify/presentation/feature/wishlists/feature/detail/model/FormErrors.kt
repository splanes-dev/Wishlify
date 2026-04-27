package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model

/**
 * Marker interface for wishlist item form validation errors.
 */
sealed interface WishlistItemFormError

/**
 * Validation errors for the item name field.
 */
sealed interface NameWishlistItemFormError : WishlistItemFormError {
  data object Length : NameWishlistItemFormError
}

/**
 * Validation errors for the item description field.
 */
sealed interface DescriptionWishlistItemFormError : WishlistItemFormError {
  data object Length : DescriptionWishlistItemFormError
}

/**
 * Validation errors for the item store field.
 */
sealed interface StoreWishlistItemFormError : WishlistItemFormError {
  data object Length : StoreWishlistItemFormError
}

/**
 * Validation errors for the item price field.
 */
sealed interface PriceWishlistItemFormError : WishlistItemFormError {
  data object Blank : PriceWishlistItemFormError
  data object Invalid : PriceWishlistItemFormError
}

/**
 * Validation errors for the item amount field.
 */
sealed interface AmountWishlistItemFormError : WishlistItemFormError {
  data object Invalid : AmountWishlistItemFormError
}

/**
 * Validation errors for the item link field.
 */
sealed interface LinkWishlistItemFormError : WishlistItemFormError {
  data object Invalid : LinkWishlistItemFormError
}

/**
 * Validation errors for the item tags field.
 */
sealed interface TagsWishlistItemFormError : WishlistItemFormError {
  data object Count : TagsWishlistItemFormError
}

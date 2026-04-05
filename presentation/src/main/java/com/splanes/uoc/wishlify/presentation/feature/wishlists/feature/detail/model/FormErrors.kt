package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model

sealed interface WishlistItemFormError

sealed interface NameWishlistItemFormError : WishlistItemFormError {
  data object Length : NameWishlistItemFormError
}

sealed interface DescriptionWishlistItemFormError : WishlistItemFormError {
  data object Length : DescriptionWishlistItemFormError
}

sealed interface StoreWishlistItemFormError : WishlistItemFormError {
  data object Length : StoreWishlistItemFormError
}

sealed interface PriceWishlistItemFormError : WishlistItemFormError {
  data object Blank : PriceWishlistItemFormError
  data object Invalid : PriceWishlistItemFormError
}

sealed interface AmountWishlistItemFormError : WishlistItemFormError {
  data object Invalid : AmountWishlistItemFormError
}

sealed interface LinkWishlistItemFormError : WishlistItemFormError {
  data object Invalid : LinkWishlistItemFormError
}

sealed interface TagsWishlistItemFormError : WishlistItemFormError {
  data object Count : TagsWishlistItemFormError
}
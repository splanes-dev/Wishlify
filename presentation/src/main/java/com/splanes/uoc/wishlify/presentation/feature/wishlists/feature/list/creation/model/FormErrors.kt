package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model

sealed interface WishlistFormError

sealed interface NameWishlistFormError : WishlistFormError {
  data object Length : NameWishlistFormError
}

sealed interface TargetWishlistFormError : WishlistFormError {
  data object Blank : TargetWishlistFormError
}

sealed interface DescriptionWishlistFormError : WishlistFormError {
  data object Length : DescriptionWishlistFormError
}
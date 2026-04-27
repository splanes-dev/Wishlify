package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model

/**
 * Shared payload used to prefill the new item flow before the user chooses the target wishlist.
 */
sealed interface WishlistNewItemByShare {
  data class Uri(val uri: String): WishlistNewItemByShare
  data class Url(val url: String): WishlistNewItemByShare
}

package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model

sealed interface WishlistNewItemByShare {
  data class Uri(val uri: String): WishlistNewItemByShare
  data class Url(val url: String): WishlistNewItemByShare
}
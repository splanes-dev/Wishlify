package com.splanes.uoc.wishlify.domain.common.media.model

sealed interface ImageMediaPath {
  data class WishlistCover(val wishlistId: String): ImageMediaPath
  data class WishlistItem(val wishlistId: String, val itemId: String): ImageMediaPath
  data class Group(val groupId: String): ImageMediaPath
}
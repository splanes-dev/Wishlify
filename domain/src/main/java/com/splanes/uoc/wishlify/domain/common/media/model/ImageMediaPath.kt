package com.splanes.uoc.wishlify.domain.common.media.model

sealed interface ImageMediaPath {
  data class WishlistCover(val wishlistId: String): ImageMediaPath
}
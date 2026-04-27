package com.splanes.uoc.wishlify.domain.common.media.model

/**
 * Logical media destination used by the domain to identify where an image
 * belongs inside the application.
 *
 * It abstracts storage details away from features and repositories.
 */
sealed interface ImageMediaPath {
  data class WishlistCover(val wishlistId: String): ImageMediaPath
  data class WishlistItem(val wishlistId: String, val itemId: String): ImageMediaPath
  data class Group(val groupId: String): ImageMediaPath
  data class SecretSanta(val secretSantaId: String): ImageMediaPath
  data class Profile(val uid: String): ImageMediaPath
}

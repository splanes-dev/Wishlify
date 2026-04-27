package com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation

import kotlinx.serialization.Serializable

/**
 * Root navigation route for the wishlists feature and its nested destinations.
 */
@Serializable
data object Wishlists {

  /**
   * Wishlists list.
   */
  @Serializable
  data object List

  /**
   * Category administration flow.
   */
  @Serializable
  data object Categories

  /**
   * Wishlist creation flow.
   */
  @Serializable
  data class NewList(val isOwn: Boolean)

  /**
   * Wishlist edition flow.
   */
  @Serializable
  data class EditList(val wishlistId: String)

  /**
   * Wishlist sharing flow.
   */
  @Serializable
  data class ShareList(val wishlistId: String)

  /**
   * Own or third-party wishlist detail.
   */
  @Serializable
  data class Detail(val id: String, val name: String)

  /**
   * Detail of a wishlist currently shared by its owner.
   */
  @Serializable
  data class DetailShared(val id: String, val name: String, val target: String?)

  /**
   * Wishlist item creation flow.
   */
  @Serializable
  data class NewItem(
    val wishlistId: String,
    val link: String?,
    val imageUrl: String? = null
  )

  /**
   * Wishlist item edition flow.
   */
  @Serializable
  data class EditItem(val wishlistId: String, val itemId: String)
}

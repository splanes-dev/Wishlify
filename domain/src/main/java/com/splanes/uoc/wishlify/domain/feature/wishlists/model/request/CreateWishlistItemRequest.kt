package com.splanes.uoc.wishlify.domain.feature.wishlists.model.request

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem

/** Input required to create a new item inside a wishlist. */
data class CreateWishlistItemRequest(
  val wishlist: String,
  val id: String,
  val name: String,
  val store: String,
  val price: Float,
  val amount: Int,
  val priority: WishlistItem.Priority,
  val link: String,
  val description: String,
  val tags: List<String>,
  val photo: ImageMediaRequest?
)

package com.splanes.uoc.wishlify.domain.feature.wishlists.model.request

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem

data class UpdateWishlistItemRequest(
  val wishlist: String,
  val currentItem: WishlistItem,
  val photo: ImageMediaRequest?,
  val name: String,
  val store: String,
  val price: Float,
  val amount: Int,
  val priority: WishlistItem.Priority,
  val link: String,
  val description: String,
  val tags: List<String>,
  val purchased: PurchaseRequest?,
) {

  sealed interface PurchaseRequest
  data object Purchased : PurchaseRequest
  data object Available : PurchaseRequest
}
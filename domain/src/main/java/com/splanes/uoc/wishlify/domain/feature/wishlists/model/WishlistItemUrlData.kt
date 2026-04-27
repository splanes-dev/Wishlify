package com.splanes.uoc.wishlify.domain.feature.wishlists.model

/** Product metadata extracted from an item URL. */
data class WishlistItemUrlData(
  val imageUrl: String?,
  val product: String?,
  val store: String?,
  val price: Double?,
  val link: String,
  val description: String?
)

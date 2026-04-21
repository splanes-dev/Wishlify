package com.splanes.uoc.wishlify.domain.feature.wishlists.model

data class WishlistItemUrlData(
  val imageUrl: String?,
  val product: String?,
  val store: String?,
  val price: Double?,
  val link: String,
  val description: String?
)
package com.splanes.uoc.wishlify.domain.feature.shared.model

data class SharedWishlistSendMessageRequest(
  val wishlist: String,
  val text: String,
)
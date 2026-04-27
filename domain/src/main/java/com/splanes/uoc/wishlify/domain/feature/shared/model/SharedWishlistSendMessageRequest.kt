package com.splanes.uoc.wishlify.domain.feature.shared.model

/** Input required to send a chat message to a shared wishlist conversation. */
data class SharedWishlistSendMessageRequest(
  val wishlist: String,
  val text: String,
)

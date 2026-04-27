package com.splanes.uoc.wishlify.domain.feature.wishlists.model.request

import com.splanes.uoc.wishlify.domain.common.model.InviteLink

/** Input required to share a private wishlist with other participants. */
data class ShareWishlistRequest(
  val wishlistId: String,
  val owner: String,
  val editors: List<String>,
  val group: String?,
  val shareLink: InviteLink,
  val editorsCanSeeUpdates: Boolean,
  val deadline: Long
)

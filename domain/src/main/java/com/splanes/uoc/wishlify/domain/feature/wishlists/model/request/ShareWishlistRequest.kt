package com.splanes.uoc.wishlify.domain.feature.wishlists.model.request

import com.splanes.uoc.wishlify.domain.common.model.InviteLink

data class ShareWishlistRequest(
  val wishlistId: String,
  val owner: String,
  val editors: List<String>,
  val group: String?,
  val shareLink: InviteLink,
  val editorsCanSeeUpdates: Boolean,
  val deadline: Long
)
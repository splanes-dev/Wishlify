package com.splanes.uoc.wishlify.data.feature.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SharedWishlistEntity(
  @SerialName("id") val id: String,
  @SerialName("wishlist") val wishlist: String,
  @SerialName("owner") val owner: String,
  @SerialName("editors") val editors: List<String>,
  @SerialName("group") val group: String?,
  @SerialName("participants") val participants: List<String>,
  @SerialName("editorsCanSeeUpdates") val editorsCanSeeUpdates: Boolean,
  @SerialName("inviteLink") val inviteLink: String,
  @SerialName("deadline") val deadline: Long,
  @SerialName("sharedAt") val sharedAt: Long,
)
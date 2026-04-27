package com.splanes.uoc.wishlify.data.feature.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Serializable Firestore persistence model for a shared wishlist header. */
@Serializable
data class SharedWishlistEntity(
  @SerialName("id") val id: String = "",
  @SerialName("wishlist") val wishlist: String = "",
  @SerialName("owner") val owner: String = "",
  @SerialName("editors") val editors: List<String> = emptyList(),
  @SerialName("group") val group: String? = null,
  @SerialName("participants") val participants: List<String> = emptyList(),
  @SerialName("editorsCanSeeUpdates") val editorsCanSeeUpdates: Boolean = false,
  @SerialName("inviteLink") val inviteLink: String = "",
  @SerialName("deadline") val deadline: Long = 0L,
  @SerialName("sharedAt") val sharedAt: Long = 0L,
)

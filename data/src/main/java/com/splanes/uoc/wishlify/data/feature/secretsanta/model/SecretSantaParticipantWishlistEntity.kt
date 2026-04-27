package com.splanes.uoc.wishlify.data.feature.secretsanta.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Serializable Firestore model for a participant wishlist shared in Secret Santa. */
@Serializable
data class SecretSantaParticipantWishlistEntity(
  @SerialName("wishlist") val wishlist: String = "",
  @SerialName("title") val title: String = ""
)

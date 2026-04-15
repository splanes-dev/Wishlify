package com.splanes.uoc.wishlify.data.feature.secretsanta.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecretSantaParticipantWishlistEntity(
  @SerialName("wishlist") val wishlist: String = ""
)

package com.splanes.uoc.wishlify.data.feature.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Serializable Firestore model for a shared-wishlist chat message. */
@Serializable
data class SharedWishlistChatMessageEntity(
  @SerialName("id") val id: String = "",
  @SerialName("type") val type: Type = Type.User,
  @SerialName("text") val text: String = "",
  @SerialName("createdBy") val createdBy: String = "",
  @SerialName("createdAt") val createdAt: Long = 0L,
) {

  /** Persisted origin of the shared-wishlist chat message. */
  @Serializable
  enum class Type {
    @SerialName("user") User,
    @SerialName("system") System,
  }
}

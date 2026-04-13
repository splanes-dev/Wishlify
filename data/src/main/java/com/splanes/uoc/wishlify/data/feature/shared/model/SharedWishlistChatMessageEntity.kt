package com.splanes.uoc.wishlify.data.feature.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SharedWishlistChatMessageEntity(
  @SerialName("id") val id: String = "",
  @SerialName("type") val type: Type = Type.User,
  @SerialName("text") val text: String = "",
  @SerialName("createdBy") val createdBy: String = "",
  @SerialName("createdAt") val createdAt: Long = 0L,
) {

  @Serializable
  enum class Type {
    @SerialName("user") User,
    @SerialName("system") System,
  }
}
package com.splanes.uoc.wishlify.data.feature.secretsanta.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecretSantaChatMessageEntity(
  @SerialName("id") val id: String = "",
  @SerialName("sender") val sender: String = "",
  @SerialName("text") val text: String = "",
  @SerialName("createdAt") val createdAt: Long = 0L
)

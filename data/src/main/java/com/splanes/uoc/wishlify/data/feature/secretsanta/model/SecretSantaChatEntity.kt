package com.splanes.uoc.wishlify.data.feature.secretsanta.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecretSantaChatEntity(
  @SerialName("id") val id: String = "",
  @SerialName("receiver") val receiver: String = "",
  @SerialName("giver") val giver: String = "",
  @SerialName("createdAt") val createdAt: Long = 0L
)
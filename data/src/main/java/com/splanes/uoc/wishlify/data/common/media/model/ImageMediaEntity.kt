package com.splanes.uoc.wishlify.data.common.media.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageMediaEntity(
  @SerialName("type") val type: Type = Type.Url,
  @SerialName("value") val value: String = "",
) {
  @Serializable
  enum class Type {
    @SerialName("Url") Url,
    @SerialName("Preset") Preset,
  }
}
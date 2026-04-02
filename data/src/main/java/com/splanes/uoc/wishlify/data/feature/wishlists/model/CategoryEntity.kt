package com.splanes.uoc.wishlify.data.feature.wishlists.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryEntity(
  @SerialName("id") val id: String = "",
  @SerialName("name") val name: String = "",
  @SerialName("color") val color: String = "",
)

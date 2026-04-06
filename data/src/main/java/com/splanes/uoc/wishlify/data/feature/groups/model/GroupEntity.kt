package com.splanes.uoc.wishlify.data.feature.groups.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupEntity(
  @SerialName("id") val id: String,
  @SerialName("name") val name: String,
  @SerialName("photoUrl") val photoUrl: String,
  @SerialName("members") val members: List<String>,
  @SerialName("createdBy") val createdBy: String,
  @SerialName("createdAt") val createdAt: Long
)
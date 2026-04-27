package com.splanes.uoc.wishlify.data.feature.groups.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Serializable Firestore persistence model for groups. */
@Serializable
data class GroupEntity(
  @SerialName("id") val id: String = "",
  @SerialName("name") val name: String = "",
  @SerialName("photoUrl") val photoUrl: String? = null,
  @SerialName("members") val members: List<String> = emptyList(),
  @SerialName("createdBy") val createdBy: String = "",
  @SerialName("createdAt") val createdAt: Long = 0L
)

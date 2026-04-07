package com.splanes.uoc.wishlify.data.feature.user.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UidByEmailEntity(
  @SerialName("uid") val uid: String = "",
)
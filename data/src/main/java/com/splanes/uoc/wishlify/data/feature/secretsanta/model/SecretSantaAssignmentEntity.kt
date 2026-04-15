package com.splanes.uoc.wishlify.data.feature.secretsanta.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecretSantaAssignmentEntity(
  @SerialName("target") val target: String = "",
)

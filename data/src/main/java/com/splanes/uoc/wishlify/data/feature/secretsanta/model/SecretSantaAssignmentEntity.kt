package com.splanes.uoc.wishlify.data.feature.secretsanta.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Serializable Firestore model for a Secret Santa participant assignment. */
@Serializable
data class SecretSantaAssignmentEntity(
  @SerialName("receiver") val receiver: String = "",
  @SerialName("giver") val giver: String = "",
)

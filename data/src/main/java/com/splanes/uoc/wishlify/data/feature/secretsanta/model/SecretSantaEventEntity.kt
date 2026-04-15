package com.splanes.uoc.wishlify.data.feature.secretsanta.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecretSantaEventEntity(
  @SerialName("id") val id: String = "",
  @SerialName("name") val name: String = "",
  @SerialName("photoUrl") val photoUrl: String? = null,
  @SerialName("budget") val budget: Double = 0.0,
  @SerialName("budgetApproximate") val budgetApproximate: Boolean = false,
  @SerialName("deadline") val deadline: Long = 0L,
  @SerialName("createdBy") val createdBy: String = "",
  @SerialName("createdAt") val createdAt: Long = 0L,
  @SerialName("group") val group: String? = null,
  @SerialName("participants") val participants: List<String> = emptyList(),
  @SerialName("inviteLink") val inviteLink: String = "",
  @SerialName("exclusions") val exclusions: Map<String, List<String>> = emptyMap(),
  @SerialName("drawStatus") val drawStatus: DrawStatus = DrawStatus.Pending
) {

  @Serializable
  enum class DrawStatus {
    @SerialName("pending")
    Pending,
    @SerialName("done")
    Done
  }
}

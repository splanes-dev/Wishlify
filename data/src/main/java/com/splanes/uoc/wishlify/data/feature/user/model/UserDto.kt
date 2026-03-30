package com.splanes.uoc.wishlify.data.feature.user.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
  @SerialName("uid") val uid: String,
  @SerialName("username") val username: String,
  @SerialName("photoUrl") val photoUrl: String?,
  @SerialName("hobbies") val hobbies: HobbiesDto,
  @SerialName("rewards") val rewards: RewardsDto,
  @SerialName("notifications") val notifications: NotificationsDto,
  @SerialName("metadata") val metadata: MetadataDto,
) {
  @Serializable
  data class HobbiesDto(
    @SerialName("enabled") val enabled: Boolean,
    @SerialName("values") val values: List<String>,
  )

  @Serializable
  data class RewardsDto(
    @SerialName("points") val points: Int,
    @SerialName("purchased") val purchased: List<String>,
  )

  @Serializable
  data class NotificationsDto(
    @SerialName("sharedWishlistChat") val sharedWishlistChat: Boolean,
    @SerialName("sharedWishlistUpdates") val sharedWishlistUpdates: Boolean,
    @SerialName("sharedWishlistsDeadlineReminders") val sharedWishlistsDeadlineReminders: Boolean,
    @SerialName("secretSantaChat") val secretSantaChat: Boolean,
    @SerialName("secretSantaDeadlineReminders") val secretSantaDeadlineReminders: Boolean,
  )

  @Serializable
  data class MetadataDto(
    @SerialName("createdAt") val createdAt: Long,
    @SerialName("lastAccess") val lastAccess: Long,
  )
}

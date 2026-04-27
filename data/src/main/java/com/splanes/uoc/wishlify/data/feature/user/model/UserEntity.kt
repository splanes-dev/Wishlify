package com.splanes.uoc.wishlify.data.feature.user.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Serializable Firestore persistence model for a user profile. */
@Serializable
data class UserEntity(
  @SerialName("uid") val uid: String = "",
  @SerialName("username") val username: String = "",
  @SerialName("photoUrl") val photoUrl: String? = null,
  @SerialName("code") val code: String = "",
  @SerialName("token") val token: String = "",
  @SerialName("hobbies") val hobbies: Hobbies = Hobbies(),
  @SerialName("rewards") val rewards: Rewards = Rewards(),
  @SerialName("notifications") val notifications: Notifications = Notifications(),
  @SerialName("metadata") val metadata: Metadata = Metadata(),
) {
  /** Persisted user hobbies configuration. */
  @Serializable
  data class Hobbies(
    @SerialName("enabled") val enabled: Boolean = false,
    @SerialName("values") val values: List<String> = emptyList(),
  )

  /** Persisted rewards snapshot associated with the user. */
  @Serializable
  data class Rewards(
    @SerialName("points") val points: Int = 0,
    @SerialName("purchased") val purchased: List<String> = emptyList(),
  )

  /** Persisted notification preferences for user-facing reminders and chats. */
  @Serializable
  data class Notifications(
    @SerialName("sharedWishlistChat") val sharedWishlistChat: Boolean = true,
    @SerialName("sharedWishlistUpdates") val sharedWishlistUpdates: Boolean = true,
    @SerialName("sharedWishlistsDeadlineReminders") val sharedWishlistsDeadlineReminders: Boolean = true,
    @SerialName("secretSantaChat") val secretSantaChat: Boolean = true,
    @SerialName("secretSantaDeadlineReminders") val secretSantaDeadlineReminders: Boolean = true,
  )

  /** Persisted metadata about profile creation and last access. */
  @Serializable
  data class Metadata(
    @SerialName("createdAt") val createdAt: Long = 0L,
    @SerialName("lastAccess") val lastAccess: Long = 0L,
  )
}

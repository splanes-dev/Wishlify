package com.splanes.uoc.wishlify.domain.feature.user.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest


/**
 * Input required to update a portion of the current user's profile.
 */
sealed class UpdateProfileRequest(open val user: User) {

  /** Profile update focused on basic account information. */
  data class BasicInfo(
    override val user: User.BasicProfile,
    val media: ImageMediaRequest?,
    val username: String,
    val email: String,
  ) : UpdateProfileRequest(user = user)

  /** Profile update focused on the user's hobbies. */
  data class Hobbies(
    override val user: User.HobbiesProfile,
    val enabled: Boolean,
    val values: List<String>
  ) : UpdateProfileRequest(user = user)

  /** Profile update focused on notification preferences. */
  data class Notifications(
    override val user: User.NotificationsProfile,
    val sharedWishlistChat: Boolean,
    val sharedWishlistUpdates: Boolean,
    val sharedWishlistsDeadlineReminders: Boolean,
    val secretSantaChat: Boolean,
    val secretSantaDeadlineReminders: Boolean,
  ) : UpdateProfileRequest(user = user)

  /** Returns the image request when the update affects basic profile information. */
  fun mediaOrNull() = when (this) {
    is BasicInfo -> media
    else -> null
  }
}

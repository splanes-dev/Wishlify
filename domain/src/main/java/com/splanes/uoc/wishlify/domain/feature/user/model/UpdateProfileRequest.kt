package com.splanes.uoc.wishlify.domain.feature.user.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest


sealed class UpdateProfileRequest(open val user: User) {

  data class BasicInfo(
    override val user: User.BasicProfile,
    val media: ImageMediaRequest?,
    val username: String,
    val email: String,
  ) : UpdateProfileRequest(user = user)

  data class Hobbies(
    override val user: User.HobbiesProfile,
    val enabled: Boolean,
    val values: List<String>
  ) : UpdateProfileRequest(user = user)

  data class Notifications(
    override val user: User.NotificationsProfile,
    val sharedWishlistChat: Boolean,
    val sharedWishlistUpdates: Boolean,
    val sharedWishlistsDeadlineReminders: Boolean,
    val secretSantaChat: Boolean,
    val secretSantaDeadlineReminders: Boolean,
  ) : UpdateProfileRequest(user = user)

  fun mediaOrNull() = when (this) {
    is BasicInfo -> media
    else -> null
  }
}
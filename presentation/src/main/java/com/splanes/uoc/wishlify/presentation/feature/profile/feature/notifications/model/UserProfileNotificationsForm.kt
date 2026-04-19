package com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.model

data class UserProfileNotificationsForm(
  val sharedWishlistChat: Boolean = false,
  val sharedWishlistUpdates: Boolean = false,
  val sharedWishlistsDeadlineReminders: Boolean = false,
  val secretSantaChat: Boolean = false,
  val secretSantaDeadlineReminders: Boolean = false,
)
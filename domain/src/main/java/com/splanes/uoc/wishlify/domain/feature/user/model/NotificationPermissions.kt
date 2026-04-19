package com.splanes.uoc.wishlify.domain.feature.user.model

data class NotificationPermissions(
  val sharedWishlistChat: Boolean,
  val sharedWishlistUpdates: Boolean,
  val sharedWishlistsDeadlineReminders: Boolean,
  val secretSantaChat: Boolean,
  val secretSantaDeadlineReminders: Boolean,
)
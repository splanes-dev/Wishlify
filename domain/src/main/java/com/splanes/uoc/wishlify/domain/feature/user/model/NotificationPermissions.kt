package com.splanes.uoc.wishlify.domain.feature.user.model

/** User notification preferences for shared wishlist and Secret Santa flows. */
data class NotificationPermissions(
  val sharedWishlistChat: Boolean,
  val sharedWishlistUpdates: Boolean,
  val sharedWishlistsDeadlineReminders: Boolean,
  val secretSantaChat: Boolean,
  val secretSantaDeadlineReminders: Boolean,
)

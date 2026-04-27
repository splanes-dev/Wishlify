package com.splanes.uoc.wishlify.presentation.feature.profile.infrastructure.navigation

import kotlinx.serialization.Serializable

/**
 * Root navigation route for the profile feature and its nested destinations.
 */
@Serializable
data object Profile {

  /**
   * Main profile screen.
   */
  @Serializable
  data object Main

  /**
   * Profile update flow.
   */
  @Serializable
  data object UpdateProfile

  /**
   * Password update flow.
   */
  @Serializable
  data object UpdatePassword

  /**
   * Hobbies administration flow.
   */
  @Serializable
  data object Hobbies

  /**
   * Notification preferences flow.
   */
  @Serializable
  data object Notifications
}

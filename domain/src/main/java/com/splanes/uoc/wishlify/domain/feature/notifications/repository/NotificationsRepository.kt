package com.splanes.uoc.wishlify.domain.feature.notifications.repository

/**
 * Repository contract for notification-related local state.
 */
interface NotificationsRepository {
  /** Returns the last time the notification permission modal was shown, if any. */
  suspend fun fetchLastTimePermissionModalShown(): Long?

  /** Persists that the notification permission modal has just been shown. */
  suspend fun updatePermissionModalShown()
}

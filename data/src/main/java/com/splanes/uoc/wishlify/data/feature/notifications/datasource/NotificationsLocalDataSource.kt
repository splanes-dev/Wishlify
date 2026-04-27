package com.splanes.uoc.wishlify.data.feature.notifications.datasource

import android.content.Context
import androidx.core.content.edit


/** Local data source for notification-related UI state persisted on device. */
class NotificationsLocalDataSource(private val context: Context) {

  private val prefs by lazy { context.getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE) }

  /** Returns when the notification permission modal was last shown, if ever. */
  fun fetchPermissionModalShownTimestamp() =
    prefs.getLong(PERMISSION_MODAL_TIMESTAMP, -1L).takeUnless { it == -1L }

  /** Persists the latest time the notification permission modal was shown. */
  fun updatePermissionModalShownTimestamp(timestamp: Long) {
    prefs.edit { putLong(PERMISSION_MODAL_TIMESTAMP, timestamp) }
  }
}

private const val PERMISSION_MODAL_TIMESTAMP = "wishlify.notifications.permission-modal-timestamp"
private const val NOTIFICATION_PREFS = "wishlify.notifications"

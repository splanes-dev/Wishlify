package com.splanes.uoc.wishlify.data.feature.notifications.datasource

import android.content.Context
import androidx.core.content.edit


class NotificationsLocalDataSource(private val context: Context) {

  private val prefs by lazy { context.getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE) }

  fun fetchPermissionModalShownTimestamp() =
    prefs.getLong(PERMISSION_MODAL_TIMESTAMP, -1L).takeUnless { it == -1L }

  fun updatePermissionModalShownTimestamp(timestamp: Long) {
    prefs.edit { putLong(PERMISSION_MODAL_TIMESTAMP, timestamp) }
  }
}

private const val PERMISSION_MODAL_TIMESTAMP = "wishlify.notifications.permission-modal-timestamp"
private const val NOTIFICATION_PREFS = "wishlify.notifications"
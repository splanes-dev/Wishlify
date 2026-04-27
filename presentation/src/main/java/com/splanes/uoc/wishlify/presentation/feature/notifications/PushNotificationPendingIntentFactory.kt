package com.splanes.uoc.wishlify.presentation.feature.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.splanes.uoc.wishlify.domain.feature.notifications.model.PushNotification

/**
 * Builds the [PendingIntent] opened when the user taps a push notification.
 */
class PushNotificationPendingIntentFactory(private val context: Context) {

  /**
   * Creates an intent that routes the user to the deeplink destination carried by the push.
   */
  fun create(push: PushNotification): PendingIntent {
    val intent = Intent(Intent.ACTION_VIEW, push.deeplink.toUri()).apply {
      flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      `package` = context.packageName
    }

    return PendingIntent.getActivity(
      context,
      push.requestCode(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
  }

  /**
   * Generates a stable request code per push type and deeplink destination.
   */
  private fun PushNotification.requestCode(): Int =
    "${this::class.qualifiedName}:${deeplink}".hashCode()
}

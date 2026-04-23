package com.splanes.uoc.wishlify.domain.feature.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import com.splanes.uoc.wishlify.domain.R

object PushNotificationChannel {
  const val CHAT = "chat"
  const val GENERAL = "general"

  object Factory {
    fun create(context: Context) {

      val manager = context.getSystemService<NotificationManager>() ?: return

      val channels = listOf(
        NotificationChannel(
          CHAT,
          context.getString(R.string.notification_channel_chat_name),
          NotificationManager.IMPORTANCE_HIGH
        ).apply {
          description = context.getString(R.string.notification_channel_chat_description)
        },

        NotificationChannel(
          GENERAL,
          context.getString(R.string.notification_channel_general_name),
          NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
          description = context.getString(R.string.notification_channel_general_description)
        }
      )

      manager.createNotificationChannels(channels)
    }
  }
}
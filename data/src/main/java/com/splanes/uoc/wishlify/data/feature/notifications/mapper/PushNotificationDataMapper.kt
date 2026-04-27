package com.splanes.uoc.wishlify.data.feature.notifications.mapper

import com.google.firebase.messaging.RemoteMessage
import com.splanes.uoc.wishlify.domain.feature.notifications.model.PushNotification

/**
 * Maps Firebase Cloud Messaging payloads into domain push notification models.
 */
class PushNotificationDataMapper {

  /**
   * Maps a [RemoteMessage] into the corresponding domain push notification.
   *
   * The mapper prefers explicit data payload fields and falls back to the
   * notification payload for title and body when needed.
   */
  fun map(message: RemoteMessage): PushNotification {
    val data = message.data

    val type = data["type"].orEmpty()

    val title = data["title"]
      ?: message.notification?.title
      ?: error("Push notification missing title")

    val body = data["body"]
      ?: message.notification?.body
      ?: error("Push notification missing body")

    val deeplink = data["deeplink"]
      ?: error("Push notification missing deeplink")

    val imageUrl = data["imageUrl"]

    return when (PushType.from(type)) {

      PushType.Chat -> PushNotification.Chat(
        title = title,
        body = body,
        deeplink = deeplink,
        imageUrl = imageUrl,
      )

      PushType.Reminder -> PushNotification.Reminder(
        title = title,
        body = body,
        deeplink = deeplink,
        imageUrl = imageUrl,
      )

      PushType.Update -> PushNotification.Update(
        title = title,
        body = body,
        deeplink = deeplink,
        imageUrl = imageUrl,
      )
    }
  }
}

/** Supported push notification types encoded in FCM data payloads. */
private enum class PushType(val value: String) {
  Chat("chat"),
  Reminder("reminder"),
  Update("update"),
  ;

  companion object {
    /** Resolves the push type from its serialized payload value. */
    fun from(value: String) = entries.first { type -> type.value.equals(value, ignoreCase = true) }
  }
}

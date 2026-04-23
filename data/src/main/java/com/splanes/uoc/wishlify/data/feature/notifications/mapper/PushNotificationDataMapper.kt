package com.splanes.uoc.wishlify.data.feature.notifications.mapper

import com.google.firebase.messaging.RemoteMessage
import com.splanes.uoc.wishlify.domain.feature.notifications.model.PushNotification

class PushNotificationDataMapper {

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

private enum class PushType(val value: String) {
  Chat("chat"),
  Reminder("reminder"),
  Update("update"),
  ;

  companion object {
    fun from(value: String) = entries.first { type -> type.value.equals(value, ignoreCase = true) }
  }
}
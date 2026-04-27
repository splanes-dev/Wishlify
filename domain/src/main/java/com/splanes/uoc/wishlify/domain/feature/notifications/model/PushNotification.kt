package com.splanes.uoc.wishlify.domain.feature.notifications.model

/**
 * Domain representation of a push notification that can be handled by the app.
 *
 * Each subtype models the intent of the notification while sharing the same
 * display and navigation payload.
 */
sealed interface PushNotification {
  val title: String
  val body: String
  val deeplink: String
  val imageUrl: String?

  /** Notification triggered by a chat message. */
  data class Chat(
    override val title: String,
    override val body: String,
    override val deeplink: String,
    override val imageUrl: String?,
  ) : PushNotification

  /** Notification used for reminders such as upcoming deadlines. */
  data class Reminder(
    override val title: String,
    override val body: String,
    override val deeplink: String,
    override val imageUrl: String?,
  ) : PushNotification

  /** Notification used for state changes or relevant content updates. */
  data class Update(
    override val title: String,
    override val body: String,
    override val deeplink: String,
    override val imageUrl: String?,
  ) : PushNotification
}

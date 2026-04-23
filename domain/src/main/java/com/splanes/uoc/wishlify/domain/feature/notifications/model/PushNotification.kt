package com.splanes.uoc.wishlify.domain.feature.notifications.model

sealed interface PushNotification {
  val title: String
  val body: String
  val deeplink: String
  val imageUrl: String?

  data class Chat(
    override val title: String,
    override val body: String,
    override val deeplink: String,
    override val imageUrl: String?,
  ) : PushNotification

  data class Reminder(
    override val title: String,
    override val body: String,
    override val deeplink: String,
    override val imageUrl: String?,
  ) : PushNotification

  data class Update(
    override val title: String,
    override val body: String,
    override val deeplink: String,
    override val imageUrl: String?,
  ) : PushNotification
}
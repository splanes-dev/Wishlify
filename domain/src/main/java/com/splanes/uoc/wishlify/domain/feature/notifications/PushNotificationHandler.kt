package com.splanes.uoc.wishlify.domain.feature.notifications

import com.splanes.uoc.wishlify.domain.feature.notifications.model.PushNotification

/**
 * Contract for presenting or dispatching a domain push notification.
 */
interface PushNotificationHandler {
  /** Handles the received [push] notification. */
  fun handle(push: PushNotification)
}

package com.splanes.uoc.wishlify.domain.feature.notifications

import com.splanes.uoc.wishlify.domain.feature.notifications.model.PushNotification

interface PushNotificationHandler {
  fun handle(push: PushNotification)
}
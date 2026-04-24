package com.splanes.uoc.wishlify.domain.feature.notifications.repository

interface NotificationsRepository {
  suspend fun fetchLastTimePermissionModalShown(): Long?
  suspend fun updatePermissionModalShown()
}
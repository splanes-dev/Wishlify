package com.splanes.uoc.wishlify.data.feature.notifications.repository

import com.splanes.uoc.wishlify.data.common.utils.nowInMillis
import com.splanes.uoc.wishlify.data.feature.notifications.datasource.NotificationsLocalDataSource
import com.splanes.uoc.wishlify.domain.feature.notifications.repository.NotificationsRepository

class NotificationsRepositoryImpl(
  private val localDataSource: NotificationsLocalDataSource
) : NotificationsRepository {

  override suspend fun fetchLastTimePermissionModalShown(): Long? =
    localDataSource.fetchPermissionModalShownTimestamp()

  override suspend fun updatePermissionModalShown() {
    localDataSource.updatePermissionModalShownTimestamp(nowInMillis())
  }
}
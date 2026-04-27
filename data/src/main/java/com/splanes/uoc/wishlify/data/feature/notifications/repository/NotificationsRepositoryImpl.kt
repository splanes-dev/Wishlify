package com.splanes.uoc.wishlify.data.feature.notifications.repository

import com.splanes.uoc.wishlify.data.common.utils.nowInMillis
import com.splanes.uoc.wishlify.data.feature.notifications.datasource.NotificationsLocalDataSource
import com.splanes.uoc.wishlify.domain.feature.notifications.repository.NotificationsRepository

/** Data-layer implementation of [NotificationsRepository] backed by local preferences. */
class NotificationsRepositoryImpl(
  private val localDataSource: NotificationsLocalDataSource
) : NotificationsRepository {

  /** Retrieves the last persisted timestamp of the permission modal display. */
  override suspend fun fetchLastTimePermissionModalShown(): Long? =
    localDataSource.fetchPermissionModalShownTimestamp()

  /** Persists the current instant as the latest permission modal display time. */
  override suspend fun updatePermissionModalShown() {
    localDataSource.updatePermissionModalShownTimestamp(nowInMillis())
  }
}

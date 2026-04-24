package com.splanes.uoc.wishlify.domain.feature.notifications.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.notifications.repository.NotificationsRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class IsPermissionModalVisibleUseCase(
  private val repository: NotificationsRepository
) : UseCase() {

  suspend operator fun invoke() =
    repository.fetchLastTimePermissionModalShown()
      .let { timestamp ->
        if (timestamp != null) {
          val zone = ZoneId.systemDefault()

          val shownAt = Instant.ofEpochMilli(timestamp)
            .atZone(zone)
            .toLocalDate()

          val today = LocalDate.now(zone)
          val maxDate = shownAt.plusDays(1)

          today.isAfter(maxDate)

        } else {
          true
        }
      }
      .also { visible ->
        if (visible) repository.updatePermissionModalShown()
      }
}
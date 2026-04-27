package com.splanes.uoc.wishlify.domain.feature.notifications.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.notifications.repository.NotificationsRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Determines whether the notification permission modal should be shown again.
 *
 * The modal is considered visible when it has never been shown or when the
 * current date is later than the day after the last display date.
 */
class IsPermissionModalVisibleUseCase(
  private val repository: NotificationsRepository
) : UseCase() {

  /**
   * Returns whether the permission modal should be displayed now.
   *
   * When the modal is visible, the last shown timestamp is updated immediately.
   */
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

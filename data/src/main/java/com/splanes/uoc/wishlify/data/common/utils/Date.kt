package com.splanes.uoc.wishlify.data.common.utils

import java.time.LocalDate
import java.time.ZoneId

/** Returns the current instant as epoch milliseconds. */
fun nowInMillis() = System.currentTimeMillis()

/**
 * Returns the epoch milliseconds for the end of day reached after [days] days
 * from the current local date.
 */
fun expirationDateInMillis(days: Long = 2): Long {
  val zone = ZoneId.systemDefault()
  return LocalDate.now(zone)
    .plusDays(days)
    .atTime(23, 59, 59)
    .atZone(zone)
    .toInstant()
    .toEpochMilli()
}

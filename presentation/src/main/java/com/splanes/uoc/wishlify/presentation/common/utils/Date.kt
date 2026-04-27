package com.splanes.uoc.wishlify.presentation.common.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

/** Formats a [Date] using one of the presentation-layer date patterns. */
fun Date.formatted(pattern: DateTimePattern = DateTimePattern.DateOnly): String =
  SimpleDateFormat(pattern.value, Locale.getDefault()).format(this)

/** Formats epoch milliseconds using one of the presentation-layer date patterns. */
fun Long.formatted(pattern: DateTimePattern = DateTimePattern.DateOnly): String =
  SimpleDateFormat(pattern.value, Locale.getDefault()).format(this)

/** Returns `true` when the date is earlier than the current instant. */
fun Date.isExpired(): Boolean =
  toInstant().isBefore(Instant.now())

/** Common date and time patterns reused by the presentation layer. */
enum class DateTimePattern(val value: String) {
  DateOnly("dd/MM/yy"),
  TimeOnly("hh:mm")
}

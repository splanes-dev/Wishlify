package com.splanes.uoc.wishlify.presentation.common.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

fun Date.formatted(pattern: DateTimePattern = DateTimePattern.DateOnly): String =
  SimpleDateFormat(pattern.value, Locale.getDefault()).format(this)

fun Long.formatted(pattern: DateTimePattern = DateTimePattern.DateOnly): String =
  SimpleDateFormat(pattern.value, Locale.getDefault()).format(this)

fun Date.isExpired(): Boolean =
  toInstant().isBefore(Instant.now())

enum class DateTimePattern(val value: String) {
  DateOnly("dd/MM/yy"),
  TimeOnly("hh:mm")
}
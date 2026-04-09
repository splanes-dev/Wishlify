package com.splanes.uoc.wishlify.data.common.utils

import java.time.LocalDate
import java.time.ZoneId

fun nowInMillis() = System.currentTimeMillis()

fun expirationDateInMillis(days: Long = 2): Long {
  val zone = ZoneId.systemDefault()
  return LocalDate.now(zone)
    .plusDays(days)
    .atTime(23, 59, 59)
    .atZone(zone)
    .toInstant()
    .toEpochMilli()
}
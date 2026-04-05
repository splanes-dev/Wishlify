package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils

import java.text.NumberFormat
import java.util.Locale

fun Float.formatPrice(
  includeCurrency: Boolean = true,
  locale: Locale = Locale.getDefault()
): String =
  if (includeCurrency) {
    NumberFormat.getCurrencyInstance(locale).format(this)
  } else {
    NumberFormat.getNumberInstance(locale).apply {
      minimumFractionDigits = 2
      maximumFractionDigits = 2
    }.format(this)
  }

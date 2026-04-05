package com.splanes.uoc.wishlify.presentation.common.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.formatted() =
  SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(this)
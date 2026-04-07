package com.splanes.uoc.wishlify.data.common.utils

import java.security.MessageDigest
import java.util.Locale

fun String.sha256(normalize: Boolean = true) =
  MessageDigest
    .getInstance("SHA-256")
    .digest(
      if (normalize) {
        trim().lowercase(Locale.ROOT)
      } else {
        this
      }.toByteArray()
    )
    .toHexString()
package com.splanes.uoc.wishlify.data.common.utils

import java.security.MessageDigest
import java.util.Locale

/**
 * Computes the SHA-256 hash of the receiver as a hexadecimal string.
 *
 * When [normalize] is true, the input is trimmed and lowercased before hashing.
 */
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

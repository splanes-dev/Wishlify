package com.splanes.uoc.wishlify.domain.common.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Generates a random UUID encoded as a hexadecimal string without separators.
 */
@OptIn(ExperimentalUuidApi::class)
fun newUuid(): String =
  Uuid.random().toHexString()

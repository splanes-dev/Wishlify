package com.splanes.uoc.wishlify.domain.common.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun newUuid(): String =
  Uuid.random().toHexString()
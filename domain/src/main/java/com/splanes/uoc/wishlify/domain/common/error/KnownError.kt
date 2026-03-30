package com.splanes.uoc.wishlify.domain.common.error

abstract class KnownError(
  override val message: String? = null,
  override val cause: Throwable? = null
) : Throwable(message, cause)
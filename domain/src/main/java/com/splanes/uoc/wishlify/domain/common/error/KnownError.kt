package com.splanes.uoc.wishlify.domain.common.error

/**
 * Base type for domain errors that are known and can be handled explicitly.
 *
 * It is used to distinguish expected failures from unexpected throwables and
 * to model application-specific error families in a typed way.
 */
abstract class KnownError(
  override val message: String? = null,
  override val cause: Throwable? = null
) : Throwable(message, cause)

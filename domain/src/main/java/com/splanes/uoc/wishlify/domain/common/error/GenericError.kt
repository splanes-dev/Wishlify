package com.splanes.uoc.wishlify.domain.common.error

/**
 * Generic error family shared across domain features.
 *
 * These errors represent cross-cutting failures that are not tied to a
 * specific business feature, such as connectivity or timeout issues.
 */
sealed class GenericError(
  override val message: String? = null,
  override val cause: Throwable? = null
) : KnownError(message, cause) {

  /** Raised when the request cannot be completed because there is no connectivity. */
  class NoInternet : GenericError()

  /** Raised when the operation exceeds the allowed execution time. */
  class RequestTimeout : GenericError()

  /** Raised when the backend fails to process the request correctly. */
  class InternalServerError : GenericError()

  /** Fallback error used when the original failure cannot be classified. */
  class Unknown(cause: Throwable? = null) : GenericError(cause = cause)
}

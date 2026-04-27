package com.splanes.uoc.wishlify.domain.feature.authentication.error

import com.splanes.uoc.wishlify.domain.common.error.KnownError

/**
 * Error family for sign-up flows.
 */
sealed class SignUpError(
  override val message: String? = null,
  override val cause: Throwable? = null,
) : KnownError(message, cause) {
  /** Fallback error used when the sign-up failure cannot be classified. */
  class Unknown(cause: Throwable? = null) : SignUpError(cause = cause)

  /** Raised when the target account already exists. */
  class UserCollision : SignUpError()

  /** Raised when the provided password does not meet security requirements. */
  class WeakPassword : SignUpError()

  /** Raised when the Google sign-up flow cannot be completed. */
  class GoogleSignUpFailed : SignUpError()
}

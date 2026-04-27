package com.splanes.uoc.wishlify.domain.feature.authentication.error

import com.splanes.uoc.wishlify.domain.common.error.KnownError

/**
 * Error family for sign-in flows.
 */
sealed class SignInError(
  override val message: String? = null,
  override val cause: Throwable? = null,
) : KnownError(message, cause) {
  /** Fallback error used when the sign-in failure cannot be classified. */
  class Unknown(cause: Throwable? = null) : SignInError(cause = cause)

  /** Raised when the provided credentials do not match any valid account. */
  class InvalidCredentials : SignInError()

  /** Raised when the email format or target account email is invalid. */
  class InvalidEmail : SignInError()

  /** Raised when an automatic sign-in attempt cannot be completed. */
  class AutoSignInFailed : SignInError()
}

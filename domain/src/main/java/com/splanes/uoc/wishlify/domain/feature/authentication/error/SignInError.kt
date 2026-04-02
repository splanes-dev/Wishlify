package com.splanes.uoc.wishlify.domain.feature.authentication.error

import com.splanes.uoc.wishlify.domain.common.error.KnownError

sealed class SignInError(
  override val message: String? = null,
  override val cause: Throwable? = null,
) : KnownError(message, cause) {
  class Unknown(cause: Throwable? = null) : SignInError(cause = cause)
  class InvalidCredentials : SignInError()
  class InvalidEmail : SignInError()
  class AutoSignInFailed : SignInError()
}
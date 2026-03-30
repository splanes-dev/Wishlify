package com.splanes.uoc.wishlify.domain.feature.authentication.error

import com.splanes.uoc.wishlify.domain.common.error.KnownError

sealed class SignUpError(
  override val message: String? = null,
  override val cause: Throwable? = null,
) : KnownError() {
  class Unknown(cause: Throwable? = null) : SignUpError(cause = cause)
  class UserCollision : SignUpError()
  class WeakPassword : SignUpError()
}
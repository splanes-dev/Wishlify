package com.splanes.uoc.wishlify.domain.feature.session.error

import com.splanes.uoc.wishlify.domain.common.error.KnownError

sealed class SessionError(
  override val message: String? = null,
  override val cause: Throwable? = null,
) : KnownError(message, cause) {

  class NoSession : SessionError()
}
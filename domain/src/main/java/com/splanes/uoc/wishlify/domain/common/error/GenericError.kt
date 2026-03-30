package com.splanes.uoc.wishlify.domain.common.error

sealed class GenericError(
  override val message: String? = null,
  override val cause: Throwable? = null
) : KnownError(message, cause) {

  class NoInternet : GenericError()

  class RequestTimeout : GenericError()

  class InternalServerError : GenericError()

  class Unknown(cause: Throwable? = null) : GenericError(cause = cause)
}
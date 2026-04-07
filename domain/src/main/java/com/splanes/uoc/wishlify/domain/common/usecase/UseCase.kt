package com.splanes.uoc.wishlify.domain.common.usecase

import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber

abstract class UseCase {
  protected open val timeout: Long = 10_000L

  suspend fun <T> execute(
    timeout: Long = this.timeout,
    block: suspend () -> Result<T>
  ): Result<T> =
    withContext(Dispatchers.Default) {
      runCatching {
        try {
          withTimeout(timeout) {
            block().getOrThrow()
          }
        } catch (err: TimeoutCancellationException) {
          Timber.e(err)
          throw GenericError.RequestTimeout()
        }
      }
    }

  companion object {
    protected const val NoTimeout = Long.MAX_VALUE
  }
}
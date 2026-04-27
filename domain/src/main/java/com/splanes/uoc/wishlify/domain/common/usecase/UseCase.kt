package com.splanes.uoc.wishlify.domain.common.usecase

import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

/**
 * Base class for domain use cases.
 *
 * It centralizes the common execution policy used across the domain layer:
 * running work on [Dispatchers.Default], enforcing a timeout, and mapping
 * coroutine timeout failures to [GenericError.RequestTimeout].
 */
abstract class UseCase {
  protected open val timeout: Long = 10_000L

  /**
   * Executes the use case block with the configured timeout.
   *
   * @param timeout Maximum time allowed for the operation in milliseconds.
   * Defaults to [timeout].
   * @param block Suspended block that contains the use case logic and returns
   * a [Result].
   * @return The original successful [Result], or a failed one if the block
   * throws, including [GenericError.RequestTimeout] when the timeout is exceeded.
   */
  suspend fun <T> execute(
    timeout: Long = this.timeout,
    block: suspend () -> Result<T>
  ): Result<T> =
    withContext(Dispatchers.Default) {
      runCatching {
        try {
          withTimeout(timeout.milliseconds) {
            block().getOrThrow()
          }
        } catch (err: TimeoutCancellationException) {
          Timber.e(err)
          throw GenericError.RequestTimeout()
        }
      }
    }

  companion object {
    /** Sentinel value used by use cases that must opt out of timeout enforcement. */
    protected const val NoTimeout = Long.MAX_VALUE
  }
}

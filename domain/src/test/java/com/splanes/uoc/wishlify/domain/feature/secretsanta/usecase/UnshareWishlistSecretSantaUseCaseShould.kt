package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UnshareWishlistSecretSantaUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: SecretSantaRepository = mock()

  private lateinit var useCase: UnshareWishlistSecretSantaUseCase

  @Before
  fun setup() {
    useCase = UnshareWishlistSecretSantaUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase("event-id")

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return failure when unshare wishlist fails`() = runTest {
    val uid = "uid"
    val eventId = "event-id"
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.unshareWishlistToGiver(uid, eventId))
      .thenThrow(error)

    val result = useCase(eventId)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).unshareWishlistToGiver(uid, eventId)
  }

  @Test
  fun `return success when unshare wishlist succeeds`() = runTest {
    val uid = "uid"
    val eventId = "event-id"

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.unshareWishlistToGiver(uid, eventId))
      .thenReturn(Result.success(Unit))

    val result = useCase(eventId)

    assertThat(result.isSuccess).isTrue()
    verify(repository).unshareWishlistToGiver(uid, eventId)
  }
}
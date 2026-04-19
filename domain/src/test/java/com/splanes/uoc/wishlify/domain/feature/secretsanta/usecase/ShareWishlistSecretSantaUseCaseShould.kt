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

class ShareWishlistSecretSantaUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: SecretSantaRepository = mock()

  private lateinit var useCase: ShareWishlistSecretSantaUseCase

  @Before
  fun setup() {
    useCase = ShareWishlistSecretSantaUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(
      eventId = "event-id",
      wishlistId = "wishlist-id"
    )

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return failure when share wishlist fails`() = runTest {
    val uid = "uid"
    val eventId = "event-id"
    val wishlistId = "wishlist-id"
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.shareWishlistToGiver(uid, eventId, wishlistId))
      .thenReturn(Result.failure(error))

    val result = useCase(
      eventId = eventId,
      wishlistId = wishlistId
    )

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).shareWishlistToGiver(uid, eventId, wishlistId)
  }

  @Test
  fun `return success when share wishlist succeeds`() = runTest {
    val uid = "uid"
    val eventId = "event-id"
    val wishlistId = "wishlist-id"

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.shareWishlistToGiver(uid, eventId, wishlistId))
      .thenReturn(Result.success(Unit))

    val result = useCase(
      eventId = eventId,
      wishlistId = wishlistId
    )

    assertThat(result.isSuccess).isTrue()
    verify(repository).shareWishlistToGiver(uid, eventId, wishlistId)
  }
}
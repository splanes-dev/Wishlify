package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SubscribeSharedWishlistsUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: SharedWishlistsRepository = mock()

  private lateinit var useCase: SubscribeSharedWishlistsUseCase

  @Before
  fun setup() {
    useCase = SubscribeSharedWishlistsUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return shared wishlists stream when everything succeeds`() = runTest {
    val uid = "uid"
    val flow = flowOf(listOf(mock<SharedWishlist>()))

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.subscribeToSharedWishlists(uid)).thenReturn(flow)

    val result = useCase()

    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(flow)
    verify(repository).subscribeToSharedWishlists(uid)
  }
}

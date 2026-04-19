package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistType
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FetchSharedWishlistsUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: SharedWishlistsRepository = mock()

  private lateinit var useCase: FetchSharedWishlistsUseCase

  @Before
  fun setup() {
    useCase = FetchSharedWishlistsUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(SharedWishlistType.Own)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return failure when fetch shared wishlists fails`() = runTest {
    val uid = "uid"
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.fetchSharedWishlists(uid))
      .thenReturn(Result.failure(error))

    val result = useCase(SharedWishlistType.Own)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).fetchSharedWishlists(uid)
  }

  @Test
  fun `return shared wishlists when everything succeeds`() = runTest {
    val uid = "uid"
    val wishlists = listOf(mock<SharedWishlist>())

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.fetchSharedWishlists(uid))
      .thenReturn(Result.success(wishlists))

    val result = useCase(SharedWishlistType.Own)

    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(wishlists)
    verify(repository).fetchSharedWishlists(uid)
  }
}
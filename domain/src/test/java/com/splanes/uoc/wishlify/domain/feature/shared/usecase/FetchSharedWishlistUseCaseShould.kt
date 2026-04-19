package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FetchSharedWishlistUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: SharedWishlistsRepository = mock()

  private lateinit var useCase: FetchSharedWishlistUseCase

  @Before
  fun setup() {
    useCase = FetchSharedWishlistUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase("shared-id")

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return failure when fetch shared wishlist fails`() = runTest {
    val uid = "uid"
    val sharedWishlistId = "shared-id"
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.fetchSharedWishlist(uid, sharedWishlistId))
      .thenReturn(Result.failure(error))

    val result = useCase(sharedWishlistId)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).fetchSharedWishlist(uid, sharedWishlistId)
  }

  @Test
  fun `return shared wishlist when everything succeeds`() = runTest {
    val uid = "uid"
    val sharedWishlistId = "shared-id"
    val sharedWishlist = mock<SharedWishlist>()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.fetchSharedWishlist(uid, sharedWishlistId))
      .thenReturn(Result.success(sharedWishlist))

    val result = useCase(sharedWishlistId)

    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(sharedWishlist)
    verify(repository).fetchSharedWishlist(uid, sharedWishlistId)
  }
}
package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistType
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FetchWishlistsUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: WishlistsRepository = mock()

  private lateinit var useCase: FetchWishlistsUseCase

  @Before
  fun setup() {
    useCase = FetchWishlistsUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(WishlistType.Own)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return failure when fetch wishlists fails`() = runTest {
    val uid = "uid"
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.fetchWishlists(uid))
      .thenReturn(Result.failure(error))

    val result = useCase(WishlistType.Own)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).fetchWishlists(uid)
  }

  @Test
  fun `return wishlists when everything succeeds`() = runTest {
    val uid = "uid"
    val wishlists = listOf(mock<Wishlist>())

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.fetchWishlists(uid))
      .thenReturn(Result.success(wishlists))

    val result = useCase(WishlistType.Own)

    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(wishlists)
    verify(repository).fetchWishlists(uid)
  }

  @Test
  fun `use all as default wishlist type`() = runTest {
    val uid = "uid"
    val wishlists = listOf(mock<Wishlist>())

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.fetchWishlists(uid))
      .thenReturn(Result.success(wishlists))

    val result = useCase()

    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(wishlists)
    verify(repository).fetchWishlists(uid)
  }
}
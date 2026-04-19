package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FetchWishlistUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val wishlistsRepository: WishlistsRepository = mock()

  private lateinit var useCase: FetchWishlistUseCase

  @Before
  fun setup() {
    useCase = FetchWishlistUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      wishlistsRepository = wishlistsRepository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase("wishlist-id")

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return failure when fetch wishlist fails`() = runTest {
    val uid = "uid"
    val wishlistId = "wishlist-id"
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(wishlistsRepository.fetchWishlist(uid, wishlistId))
      .thenReturn(Result.failure(error))

    val result = useCase(wishlistId)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(wishlistsRepository).fetchWishlist(uid, wishlistId)
  }

  @Test
  fun `return wishlist when everything succeeds`() = runTest {
    val uid = "uid"
    val wishlistId = "wishlist-id"
    val wishlist = mock<Wishlist>()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(wishlistsRepository.fetchWishlist(uid, wishlistId))
      .thenReturn(Result.success(wishlist))

    val result = useCase(wishlistId)

    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(wishlist)
    verify(wishlistsRepository).fetchWishlist(uid, wishlistId)
  }
}
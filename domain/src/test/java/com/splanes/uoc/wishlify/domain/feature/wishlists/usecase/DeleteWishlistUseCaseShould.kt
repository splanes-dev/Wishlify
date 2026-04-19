package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DeleteWishlistUseCaseShould {

  private val mediaRepository: ImageMediaRepository = mock()
  private val repository: WishlistsRepository = mock()

  private lateinit var useCase: DeleteWishlistUseCase

  @Before
  fun setup() {
    useCase = DeleteWishlistUseCase(
      mediaRepository = mediaRepository,
      repository = repository,
    )
  }

  @Test
  fun `return failure when delete media throws exception`() = runTest {
    val wishlist = wishlist()
    val error = RuntimeException()

    whenever(mediaRepository.delete(ImageMediaPath.WishlistCover(wishlist.id)))
      .thenThrow(error)

    val result = useCase(wishlist)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return failure when delete wishlist fails`() = runTest {
    val wishlist = wishlist()
    val error = RuntimeException()

    whenever(mediaRepository.delete(ImageMediaPath.WishlistCover(wishlist.id)))
      .thenReturn(Result.success(Unit))
    whenever(repository.deleteWishlist(wishlist.id))
      .thenReturn(Result.failure(error))

    val result = useCase(wishlist)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).deleteWishlist(wishlist.id)
  }

  @Test
  fun `return success when delete media and delete wishlist succeed`() = runTest {
    val wishlist = wishlist()

    whenever(mediaRepository.delete(ImageMediaPath.WishlistCover(wishlist.id)))
      .thenReturn(Result.success(Unit))
    whenever(repository.deleteWishlist(wishlist.id))
      .thenReturn(Result.success(Unit))

    val result = useCase(wishlist)

    assertThat(result.isSuccess).isTrue()
    verify(repository).deleteWishlist(wishlist.id)
  }

  @Test
  fun `delete media before deleting wishlist`() = runTest {
    val wishlist = wishlist()

    whenever(mediaRepository.delete(ImageMediaPath.WishlistCover(wishlist.id)))
      .thenReturn(Result.success(Unit))
    whenever(repository.deleteWishlist(wishlist.id))
      .thenReturn(Result.success(Unit))

    useCase(wishlist)

    inOrder(mediaRepository, repository) {
      verify(mediaRepository).delete(ImageMediaPath.WishlistCover(wishlist.id))
      verify(repository).deleteWishlist(wishlist.id)
    }
  }

  private fun wishlist(): Wishlist =
    mock<Wishlist> {
      whenever(it.id).thenReturn("wishlist-id")
    }
}
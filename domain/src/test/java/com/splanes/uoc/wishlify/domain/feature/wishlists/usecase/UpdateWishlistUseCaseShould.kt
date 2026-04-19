package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UpdateWishlistUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: WishlistsRepository = mock()
  private val mediaRepository: ImageMediaRepository = mock()

  private lateinit var useCase: UpdateWishlistUseCase

  @Before
  fun setup() {
    useCase = UpdateWishlistUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository,
      mediaRepository = mediaRepository,
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val request = updateRequest()
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository, never()).updateWishlist(
      uid = any(),
      imageMedia = any(),
      request = any()
    )
  }

  @Test
  fun `delete previous cover and update wishlist with preset media`() = runTest {
    val uid = "uid"
    val request = updateRequest(
      media = ImageMediaRequest.Preset("preset-id")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(mediaRepository.delete(ImageMediaPath.WishlistCover(request.currentWishlist.id)))
      .thenReturn(Result.success(Unit))
    whenever(
      repository.updateWishlist(
        uid = eq(uid),
        imageMedia = eq(ImageMedia.Preset("preset-id")),
        request = eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()

    inOrder(mediaRepository, repository) {
      verify(mediaRepository).delete(ImageMediaPath.WishlistCover(request.currentWishlist.id))
      verify(repository).updateWishlist(
        uid = uid,
        imageMedia = ImageMedia.Preset("preset-id"),
        request = request
      )
    }
  }

  @Test
  fun `delete previous cover and update wishlist with url media`() = runTest {
    val uid = "uid"
    val request = updateRequest(
      media = ImageMediaRequest.Url("https://image.test/photo.jpg")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(mediaRepository.delete(ImageMediaPath.WishlistCover(request.currentWishlist.id)))
      .thenReturn(Result.success(Unit))
    whenever(
      repository.updateWishlist(
        uid = eq(uid),
        imageMedia = eq(ImageMedia.Url("https://image.test/photo.jpg")),
        request = eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()

    inOrder(mediaRepository, repository) {
      verify(mediaRepository).delete(ImageMediaPath.WishlistCover(request.currentWishlist.id))
      verify(repository).updateWishlist(
        uid = uid,
        imageMedia = ImageMedia.Url("https://image.test/photo.jpg"),
        request = request
      )
    }
  }

  @Test
  fun `return failure when update wishlist fails`() = runTest {
    val uid = "uid"
    val request = updateRequest(
      media = ImageMediaRequest.Preset("preset-id")
    )
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(mediaRepository.delete(ImageMediaPath.WishlistCover(request.currentWishlist.id)))
      .thenReturn(Result.success(Unit))
    whenever(
      repository.updateWishlist(
        uid = eq(uid),
        imageMedia = eq(ImageMedia.Preset("preset-id")),
        request = eq(request)
      )
    ).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return success when update wishlist succeeds`() = runTest {
    val uid = "uid"
    val request = updateRequest(
      media = ImageMediaRequest.Preset("preset-id")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(mediaRepository.delete(ImageMediaPath.WishlistCover(request.currentWishlist.id)))
      .thenReturn(Result.success(Unit))
    whenever(
      repository.updateWishlist(
        uid = eq(uid),
        imageMedia = eq(ImageMedia.Preset("preset-id")),
        request = eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
  }

  private fun updateRequest(
    media: ImageMediaRequest = ImageMediaRequest.Preset("preset-id")
  ) = UpdateWishlistRequest.Own(
    currentWishlist = currentWishlist(),
    media = media,
    title = "",
    description = "",
    category = null,
    editorInviteLink = mock(),
  )

  private fun currentWishlist(): Wishlist =
    mock {
      whenever(it.id).thenReturn("wishlist-id")
    }
}
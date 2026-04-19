package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CreateWishlistUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: WishlistsRepository = mock()
  private val mediaRepository: ImageMediaRepository = mock()

  private lateinit var useCase: CreateWishlistUseCase

  @Before
  fun setup() {
    useCase = CreateWishlistUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository,
      mediaRepository = mediaRepository,
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val request = createRequest()
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository, never()).addWishlist(
      uid = any(),
      imageMedia = any(),
      request = any()
    )
  }

  @Test
  fun `create wishlist with url image media when request media is url`() = runTest {
    val uid = "uid"
    val request = createRequest(
      media = ImageMediaRequest.Url("https://image.test/photo.jpg")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.addWishlist(
        uid = eq(uid),
        imageMedia = eq(ImageMedia.Url("https://image.test/photo.jpg")),
        request = eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).addWishlist(
      uid = uid,
      imageMedia = ImageMedia.Url("https://image.test/photo.jpg"),
      request = request
    )
  }

  @Test
  fun `create wishlist with preset image media when request media is preset`() = runTest {
    val uid = "uid"
    val request = createRequest(
      media = ImageMediaRequest.Preset("preset-id")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.addWishlist(
        uid = eq(uid),
        imageMedia = eq(ImageMedia.Preset("preset-id")),
        request = eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).addWishlist(
      uid = uid,
      imageMedia = ImageMedia.Preset("preset-id"),
      request = request
    )
  }

  @Test
  fun `return failure when create wishlist fails`() = runTest {
    val uid = "uid"
    val request = createRequest(
      media = ImageMediaRequest.Preset("preset-id")
    )
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.addWishlist(
        uid = eq(uid),
        imageMedia = eq(ImageMedia.Preset("preset-id")),
        request = eq(request)
      )
    ).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).addWishlist(
      uid = uid,
      imageMedia = ImageMedia.Preset("preset-id"),
      request = request
    )
  }

  @Test
  fun `return success when create wishlist succeeds`() = runTest {
    val uid = "uid"
    val request = createRequest(
      media = ImageMediaRequest.Preset("preset-id")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.addWishlist(
        uid = eq(uid),
        imageMedia = eq(ImageMedia.Preset("preset-id")),
        request = eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
  }

  private fun createRequest(
    id: String = "wishlist-id",
    title: String = "Wishlist",
    media: ImageMediaRequest = ImageMediaRequest.Preset("preset-id"),
    description: String = "",
    category: Category? = null,
    editorInviteLink: InviteLink = InviteLink(token = "", origin = InviteLink.WishlistsEditor),
  ) = CreateWishlistRequest.Own(
    id = id,
    title = title,
    media = media,
    description = description,
    category = category,
    editorInviteLink = editorInviteLink,
  )
}
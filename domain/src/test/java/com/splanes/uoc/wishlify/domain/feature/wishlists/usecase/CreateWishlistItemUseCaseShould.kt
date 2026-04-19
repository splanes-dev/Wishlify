package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistItemRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CreateWishlistItemUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: WishlistsRepository = mock()
  private val mediaRepository: ImageMediaRepository = mock()

  private lateinit var useCase: CreateWishlistItemUseCase

  @Before
  fun setup() {
    useCase = CreateWishlistItemUseCase(
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
    verify(repository, never()).addWishlistItem(any(), anyOrNull(), any())
  }

  @Test
  fun `create wishlist item with no photo when request photo is null`() = runTest {
    val uid = "uid"
    val request = createRequest(photo = null)

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.addWishlistItem(uid, null, request))
      .thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).addWishlistItem(uid, null, request)
  }

  @Test
  fun `create wishlist item with url image media when request photo is url`() = runTest {
    val uid = "uid"
    val request = createRequest(
      photo = ImageMediaRequest.Url("https://image.test/photo.jpg")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.addWishlistItem(
        eq(uid),
        eq(ImageMedia.Url("https://image.test/photo.jpg")),
        eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).addWishlistItem(
      uid,
      ImageMedia.Url("https://image.test/photo.jpg"),
      request
    )
  }

  @Test
  fun `create wishlist item with preset image media when request photo is preset`() = runTest {
    val uid = "uid"
    val request = createRequest(
      photo = ImageMediaRequest.Preset("preset-id")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.addWishlistItem(
        eq(uid),
        eq(ImageMedia.Preset("preset-id")),
        eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).addWishlistItem(
      uid,
      ImageMedia.Preset("preset-id"),
      request
    )
  }

  @Test
  fun `return failure when create wishlist item fails`() = runTest {
    val uid = "uid"
    val request = createRequest(photo = null)
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.addWishlistItem(uid, null, request))
      .thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).addWishlistItem(uid, null, request)
  }

  private fun createRequest(
    id: String = "item-id",
    wishlist: String = "wishlist-id",
    name: String = "Item name",
    photo: ImageMediaRequest? = null,
    store: String = "",
    price: Float = 1f,
    amount: Int = 1,
    priority: WishlistItem.Priority = WishlistItem.Priority.Standard,
    link: String = "",
    description: String = "",
    tags: List<String> = emptyList(),
  ) = CreateWishlistItemRequest(
    id = id,
    wishlist = wishlist,
    name = name,
    photo = photo,
    store = store,
    price = price,
    amount = amount,
    priority = priority,
    link = link,
    description = description,
    tags = tags,
  )
}
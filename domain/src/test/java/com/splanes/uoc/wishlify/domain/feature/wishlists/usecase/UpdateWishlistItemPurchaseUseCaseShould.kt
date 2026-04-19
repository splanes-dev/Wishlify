package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistItemRequest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class UpdateWishlistItemPurchaseUseCaseShould {

  private val updateWishlistItemUseCase: UpdateWishlistItemUseCase = mock()

  private lateinit var useCase: UpdateWishlistItemPurchaseUseCase

  @Before
  fun setup() {
    useCase = UpdateWishlistItemPurchaseUseCase(updateWishlistItemUseCase)
  }

  @Test
  fun `set purchased when item is not purchased`() = runTest {
    val item = wishlistItem(purchased = null)

    whenever(updateWishlistItemUseCase(anyRequest()))
      .thenReturn(Result.success(Unit))

    useCase("wishlist-id", item)

    val captor = argumentCaptor<UpdateWishlistItemRequest>()
    verify(updateWishlistItemUseCase).invoke(captor.capture())

    assertThat(captor.firstValue.purchased)
      .isEqualTo(UpdateWishlistItemRequest.Purchased)
  }

  @Test
  fun `set available when item is already purchased`() = runTest {
    val item = wishlistItem(purchased = "someone")

    whenever(updateWishlistItemUseCase(anyRequest()))
      .thenReturn(Result.success(Unit))

    useCase("wishlist-id", item)

    val captor = argumentCaptor<UpdateWishlistItemRequest>()
    verify(updateWishlistItemUseCase).invoke(captor.capture())

    assertThat(captor.firstValue.purchased)
      .isEqualTo(UpdateWishlistItemRequest.Available)
  }

  @Test
  fun `propagate failure from update wishlist item use case`() = runTest {
    val item = wishlistItem()
    val error = RuntimeException()

    whenever(updateWishlistItemUseCase(anyRequest()))
      .thenReturn(Result.failure(error))

    val result = useCase("wishlist-id", item)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `map basic fields correctly into request`() = runTest {
    val item = wishlistItem(
      name = "Item",
      photoUrl = "http://image",
    )

    whenever(updateWishlistItemUseCase(anyRequest()))
      .thenReturn(Result.success(Unit))

    useCase("wishlist-id", item)

    val captor = argumentCaptor<UpdateWishlistItemRequest>()
    verify(updateWishlistItemUseCase).invoke(captor.capture())

    val request = captor.firstValue

    assertThat(request.wishlist).isEqualTo("wishlist-id")
    assertThat(request.name).isEqualTo("Item")
    assertThat(request.photo)
      .isEqualTo(ImageMediaRequest.Url("http://image"))
  }

  // --- helpers ---

  private fun wishlistItem(
    name: String = "name",
    photoUrl: String? = null,
    purchased: String? = null,
  ): WishlistItem =
    mock {
      on { this.name } doReturn name
      on { this.photoUrl } doReturn photoUrl
      on { this.purchased } doReturn purchased?.let {
        WishlistItem.PurchaseMetadata(
          purchasedBy = User.Basic("", it, "", null),
          purchasedAt = Date(1L)
        )
      }
      on { this.store } doReturn ""
      on { this.unitPrice } doReturn 1f
      on { this.amount } doReturn 1
      on { this.priority } doReturn mock()
      on { this.link } doReturn ""
      on { this.description } doReturn ""
      on { this.tags } doReturn emptyList()
    }

  private fun anyRequest(): UpdateWishlistItemRequest =
    any()
}
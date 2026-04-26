package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem.LinkedItem
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemStateRequest
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemUpdateStateRequest
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class UpdateSharedWishlistItemUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: SharedWishlistsRepository = mock()

  private lateinit var useCase: UpdateSharedWishlistItemUseCase

  @Before
  fun setup() {
    useCase = UpdateSharedWishlistItemUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository,
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val request = request()
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return failure when update shared wishlist item state fails`() = runTest {
    val uid = "uid"
    val request = request()
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.updateSharedWishlistItemState(uid, request))
      .thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).updateSharedWishlistItemState(uid, request)
  }

  @Test
  fun `return success when update shared wishlist item state succeeds`() = runTest {
    val uid = "uid"
    val request = request()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.updateSharedWishlistItemState(uid, request))
      .thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).updateSharedWishlistItemState(uid, request)
  }

  private fun request() = SharedWishlistItemUpdateStateRequest(
    sharedWishlist = SharedWishlist.Own(
      id = "",
      linkedWishlist = SharedWishlist.LinkedWishlist(
        id = "",
        name = "",
        photo = ImageMedia.Url(""),
        target = null,
        description = ""
      ),
      owner = User.Basic("", "", "", null),
      group = null,
      participants = emptyList(),
      editors = emptyList(),
      inviteLink = InviteLink("", InviteLink.Origin.WishlistShare),
      deadline = Date(1L),
      sharedAt = Date(1L),
      numOfItems = 1,
    ),
    item = SharedWishlistItem(
      id = "",
      linkedItem = LinkedItem(
        id = "",
        photoUrl = null,
        name = "",
        store = "",
        link = "",
        unitPrice = 1f,
        amount = 1,
        description = "",
        priority = WishlistItem.Priority.Standard,
      ),
      state = SharedWishlistItem.Available,
    ),
    newStateRequest = SharedWishlistItemStateRequest.JoinToShareRequest,
  )
}
package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class UnshareWishlistUseCaseShould {

  private val repository: SharedWishlistsRepository = mock()

  private lateinit var useCase: UnshareWishlistUseCase

  @Before
  fun setup() {
    useCase = UnshareWishlistUseCase(repository)
  }

  @Test
  fun `call repository with correct ids and return success when repository succeeds`() = runTest {
    val shared = sharedWishlist()

    whenever(
      repository.unshareSharedWishlist(shared.linkedWishlist.id)
    ).thenReturn(Result.success(Unit))

    val result = useCase(shared)

    assertThat(result.isSuccess).isTrue()
    verify(repository).unshareSharedWishlist(
      wishlistId = shared.linkedWishlist.id
    )
  }

  @Test
  fun `return failure when repository returns failure result`() = runTest {
    val shared = sharedWishlist()
    val error = RuntimeException()

    whenever(
      repository.unshareSharedWishlist(shared.linkedWishlist.id)
    ).thenReturn(Result.failure(error))

    val result = useCase(shared)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  private fun sharedWishlist() = SharedWishlist.Own(
    id = "",
    linkedWishlist = SharedWishlist.LinkedWishlist(
      id = "",
      name = "",
      photo = ImageMedia.Url(""),
      target = null
    ),
    owner = User.Basic("", "", "", null),
    group = null,
    participants = emptyList(),
    editors = emptyList(),
    inviteLink = InviteLink("", InviteLink.Origin.WishlistShare),
    deadline = Date(1L),
    sharedAt = Date(1L),
    numOfItems = 1,
  )
}
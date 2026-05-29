package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist.LinkedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.SubscribeSharedWishlistsUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class SharedWishlistsListViewModelShould : UnitTest() {

  private val subscribeSharedWishlistsUseCase: SubscribeSharedWishlistsUseCase = mock()
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: SharedWishlistsListViewModel

  @Before
  fun setup() {
    viewModel = SharedWishlistsListViewModel(
      subscribeSharedWishlistsUseCase = subscribeSharedWishlistsUseCase,
      addSharedWishlistParticipantByTokenUseCase = mock(),
      isPermissionModalVisibleUseCase = mock {
        on { invoke() } doReturn false
      },
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `fetch own shared wishlists on init and show loading then empty when there are no wishlists`() =
    runTest {
      whenever(subscribeSharedWishlistsUseCase())
        .thenReturn(Result.success(flowOf(emptyList())))

      viewModel.uiState.test {
        val loadingState = awaitItem()
        assertThat(loadingState).isEqualTo(
          SharedWishlistsListUiState.Loading
        )

        val emptyState = awaitItem()
        assertThat(emptyState).isEqualTo(SharedWishlistsListUiState.Empty)
      }
    }

  @Test
  fun `fetch own shared wishlists on init and show listing when there are results`() = runTest {
    val wishlist = ownSharedWishlist()

    whenever(subscribeSharedWishlistsUseCase())
      .thenReturn(Result.success(flowOf(listOf(wishlist))))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        SharedWishlistsListUiState.Loading
      )

      val listingState = awaitItem()
      assertThat(listingState).isInstanceOf(SharedWishlistsListUiState.Listing::class.java)
    }
  }

  @Test
  fun `show error when fetch shared wishlists fails`() = runTest {
    val error = RuntimeException()

    whenever(subscribeSharedWishlistsUseCase())
      .thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(SharedWishlistsListUiState.Empty)
    }
  }

  @Test
  fun `reload current tab shared wishlists`() = runTest {
    whenever(subscribeSharedWishlistsUseCase())
      .thenReturn(
        Result.success(flowOf(emptyList())),
        Result.success(flowOf(emptyList())),
      )

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onReloadWishlists()

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        SharedWishlistsListUiState.Loading
      )

      val emptyState = awaitItem()
      assertThat(emptyState).isEqualTo(SharedWishlistsListUiState.Empty)

      verify(subscribeSharedWishlistsUseCase, times(2)).invoke()
    }
  }

  @Test
  fun `update listing when shared wishlists stream emits new data`() = runTest {
    val wishlist = ownSharedWishlist()
    val wishlistsFlow = MutableSharedFlow<List<SharedWishlist>>(replay = 1)

    whenever(subscribeSharedWishlistsUseCase())
      .thenReturn(Result.success(wishlistsFlow))

    viewModel.uiState.test {
      assertThat(awaitItem()).isEqualTo(SharedWishlistsListUiState.Loading)

      wishlistsFlow.emit(emptyList())
      assertThat(awaitItem()).isEqualTo(SharedWishlistsListUiState.Empty)

      wishlistsFlow.emit(listOf(wishlist))
      val listingState = awaitItem()
      assertThat(listingState).isInstanceOf(SharedWishlistsListUiState.Listing::class.java)
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val error = RuntimeException()

    whenever(subscribeSharedWishlistsUseCase())
      .thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()

      viewModel.onDismissError()

      val state = awaitItem()
      assertThat(state).isEqualTo(SharedWishlistsListUiState.Empty)
    }
  }

  private fun ownSharedWishlist(
    id: String = "",
    linkedWishlist: LinkedWishlist = LinkedWishlist(
      id = "",
      name = "",
      photo = ImageMedia.Url(""),
      target = "",
      description = ""
    ),
    owner: User.Basic = User.Basic("", "", "", null),
    group: Group.Basic? = null,
    participants: List<User.Basic> = emptyList(),
    editors: List<User.Basic> = emptyList(),
    inviteLink: InviteLink = InviteLink("", InviteLink.Origin.WishlistShare),
    deadline: Date = Date(1L),
    sharedAt: Date = Date(1L),
    numOfItems: Int = 1,
  ): SharedWishlist =
    SharedWishlist.Own(
      id = id,
      linkedWishlist = linkedWishlist,
      owner = owner,
      group = group,
      participants = participants,
      editors = editors,
      inviteLink = inviteLink,
      deadline = deadline,
      sharedAt = sharedAt,
      numOfItems = numOfItems,
    )
}

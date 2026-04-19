package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist.UpdateMetadata
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist.WishlistCategory
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistType
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistsUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsTab
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Date

class WishlistsListViewModelShould : UnitTest() {

  private val fetchWishlistsUseCase: FetchWishlistsUseCase = mock()
  private val deleteWishlistUseCase: DeleteWishlistUseCase = mock()
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: WishlistsListViewModel

  @Before
  fun setup() {
    viewModel = WishlistsListViewModel(
      fetchWishlistsUseCase,
      deleteWishlistUseCase,
      errorUiMapper
    )
  }

  @Test
  fun `fetch own wishlists on init and show listing when success`() = runTest {
    val wishlists = listOf(ownWishlist())

    whenever(fetchWishlistsUseCase(WishlistType.Own))
      .thenReturn(Result.success(wishlists))

    viewModel.uiState.test {
      awaitItem()
      val loading = awaitItem()
      assertThat(loading).isEqualTo(
        WishlistsListUiState.Loading(WishlistsTab.Own)
      )

      val listing = awaitItem()
      assertThat(listing).isInstanceOf(WishlistsListUiState.Listing::class.java)
    }
  }

  @Test
  fun `show empty when no wishlists`() = runTest {
    whenever(fetchWishlistsUseCase(WishlistType.Own))
      .thenReturn(Result.success(emptyList()))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      val empty = awaitItem()
      assertThat(empty).isEqualTo(
        WishlistsListUiState.Empty(
          tabSelected = WishlistsTab.Own,
          sharedWishlistFeedback = null,
          isLoading = false,
          error = null
        )
      )
    }
  }

  @Test
  fun `show error when fetch fails`() = runTest {
    val error = RuntimeException()
    whenever(fetchWishlistsUseCase(WishlistType.Own))
      .thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        WishlistsListUiState.Empty(
          tabSelected = WishlistsTab.Own,
          sharedWishlistFeedback = null,
          isLoading = false,
          error = errorUiModel()
        )
      )
    }
  }

  @Test
  fun `change tab triggers new fetch`() = runTest {
    whenever(fetchWishlistsUseCase(WishlistType.Own))
      .thenReturn(Result.success(emptyList()))

    whenever(fetchWishlistsUseCase(WishlistType.ThirdParty))
      .thenReturn(Result.success(listOf(thirdPartyWishlist())))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()
      awaitItem()

      viewModel.onTabClick(WishlistsTab.ThirdParty)

      val loading = awaitItem()
      assertThat(loading).isEqualTo(
        WishlistsListUiState.Loading(WishlistsTab.ThirdParty)
      )

      val listing = awaitItem()
      assertThat(listing).isInstanceOf(WishlistsListUiState.Listing::class.java)
    }
  }

  @Test
  fun `click same tab does not trigger fetch`() = runTest {
    whenever(fetchWishlistsUseCase(WishlistType.Own))
      .thenReturn(Result.success(emptyList()))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()
      awaitItem()

      viewModel.onTabClick(WishlistsTab.Own)

      expectNoEvents()
    }
  }

  @Test
  fun `delete wishlist removes it on success`() = runTest {
    val wishlist = ownWishlist()

    whenever(fetchWishlistsUseCase(WishlistType.Own))
      .thenReturn(Result.success(listOf(wishlist)))

    whenever(deleteWishlistUseCase(wishlist))
      .thenReturn(Result.success(Unit))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()
      awaitItem()

      viewModel.onDeleteWishlist(wishlist)

      val loading = awaitItem()
      assertThat((loading as WishlistsListUiState.Listing).isLoading).isTrue()

      val updated = awaitItem()
      assertThat((updated as WishlistsListUiState.Empty).isLoading).isFalse()
    }
  }

  @Test
  fun `show error when delete fails`() = runTest {
    val wishlist = ownWishlist()
    val error = RuntimeException()

    whenever(fetchWishlistsUseCase(WishlistType.Own))
      .thenReturn(Result.success(listOf(wishlist)))

    whenever(deleteWishlistUseCase(wishlist))
      .thenReturn(Result.failure(error))

    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {

      awaitItem()

      viewModel.onDeleteWishlist(wishlist)

      awaitItem() // loading

      val errorState = awaitItem()
      assertThat((errorState as WishlistsListUiState.Listing).error)
        .isEqualTo(errorUiModel())
    }
  }

  @Test
  fun `show shared wishlist feedback`() = runTest {
    whenever(fetchWishlistsUseCase(WishlistType.Own))
      .thenReturn(Result.success(listOf(ownWishlist())))

    viewModel.uiState.test {

      awaitItem()
      awaitItem()

      viewModel.onWishlistShared("MyList")

      awaitItem()

      val state = awaitItem()
      assertThat((state as WishlistsListUiState.Listing).sharedWishlistFeedback)
        .isEqualTo("MyList")
    }
  }

  @Test
  fun `clear shared wishlist feedback`() = runTest {
    whenever(fetchWishlistsUseCase(WishlistType.Own))
      .thenReturn(Result.success(listOf(ownWishlist())))

    viewModel.uiState.test {

      awaitItem()

      viewModel.onWishlistShared("MyList")
      awaitItem()
      awaitItem()

      viewModel.onClearSharedWishlistFeedback()

      val state = awaitItem()
      assertThat((state as WishlistsListUiState.Listing).sharedWishlistFeedback)
        .isNull()
    }
  }

  private fun ownWishlist(
    id: String = "",
    title: String = "",
    description: String = "",
    photo: ImageMedia = ImageMedia.Url(""),
    category: WishlistCategory? = null,
    editorInviteLink: InviteLink = InviteLink("", InviteLink.Origin.WishlistEditor),
    editors: List<User.Basic> = emptyList(),
    numOfNonPurchasedItems: Int = 1,
    numOfItems: Int = 1,
    createdBy: User.Basic = User.Basic(uid = "", username = "", code = "", photoUrl = null),
    createdAt: Date = Date(1L),
    lastUpdate: UpdateMetadata = UpdateMetadata(updatedBy = User.Basic("", "", "", null), Date(1L)),
  ) = Wishlist.Own(
    id = id,
    title = title,
    description = description,
    photo = photo,
    category = category,
    editorInviteLink = editorInviteLink,
    editors = editors,
    numOfNonPurchasedItems = numOfNonPurchasedItems,
    numOfItems = numOfItems,
    createdBy = createdBy,
    createdAt = createdAt,
    lastUpdate = lastUpdate,
  )

  private fun thirdPartyWishlist(
    id: String = "",
    title: String = "",
    description: String = "",
    photo: ImageMedia = ImageMedia.Url(""),
    category: WishlistCategory? = null,
    editorInviteLink: InviteLink = InviteLink("", InviteLink.Origin.WishlistEditor),
    editors: List<User.Basic> = emptyList(),
    numOfNonPurchasedItems: Int = 1,
    numOfItems: Int = 1,
    createdBy: User.Basic = User.Basic(uid = "", username = "", code = "", photoUrl = null),
    createdAt: Date = Date(1L),
    lastUpdate: UpdateMetadata = UpdateMetadata(updatedBy = User.Basic("", "", "", null), Date(1L)),
    target: String = "",
  ) = Wishlist.ThirdParty(
    id = id,
    title = title,
    description = description,
    photo = photo,
    category = category,
    editorInviteLink = editorInviteLink,
    editors = editors,
    numOfNonPurchasedItems = numOfNonPurchasedItems,
    numOfItems = numOfItems,
    createdBy = createdBy,
    createdAt = createdAt,
    lastUpdate = lastUpdate,
    target = target
  )
}
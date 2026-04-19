package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist.UpdateMetadata
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist.WishlistCategory
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem.Priority
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem.PurchaseMetadata
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.UpdateWishlistItemPurchaseUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemForm
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Date

class WishlistDetailViewModelShould : UnitTest() {

  private val wishlistId = "wishlist-id"
  private val wishlistName = "My Wishlist"

  private val fetchWishlistUseCase: FetchWishlistUseCase = mock()
  private val fetchWishlistItemsUseCase: FetchWishlistItemsUseCase = mock()
  private val fetchWishlistItemUseCase: FetchWishlistItemUseCase = mock()
  private val deleteWishlistUseCase: DeleteWishlistUseCase = mock()
  private val deleteWishlistItemUseCase: DeleteWishlistItemUseCase = mock()
  private val updateWishlistItemPurchaseUseCase: UpdateWishlistItemPurchaseUseCase = mock()
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: WishlistDetailViewModel

  @Before
  fun setup() {
    viewModel = WishlistDetailViewModel(
      wishlistId = wishlistId,
      wishlistName = wishlistName,
      fetchWishlistUseCase = fetchWishlistUseCase,
      fetchWishlistItemsUseCase = fetchWishlistItemsUseCase,
      fetchWishlistItemUseCase = fetchWishlistItemUseCase,
      deleteWishlistUseCase = deleteWishlistUseCase,
      deleteWishlistItemUseCase = deleteWishlistItemUseCase,
      updateWishlistItemPurchaseUseCase = updateWishlistItemPurchaseUseCase,
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `fetch wishlist and items on init and show listing when success`() = runTest {
    val wishlist = ownWishlist()
    val items = listOf(wishlistItem(id = "1"))

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(items))

    viewModel.uiState.test {
      assertThat(awaitItem()).isEqualTo(
        WishlistDetailUiState.Loading(wishlistName)
      )

      val state = awaitItem()
      assertThat(state).isInstanceOf(WishlistDetailUiState.Listing::class.java)
    }
  }

  @Test
  fun `show empty when wishlist exists and items are empty`() = runTest {
    val wishlist = ownWishlist()

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(emptyList()))

    viewModel.uiState.test {
      awaitItem()

      val state = awaitItem()
      assertThat(state).isEqualTo(
        WishlistDetailUiState.Empty(
          wishlistName = wishlistName,
          wishlist = wishlist,
          isNewItemByLinkModalOpen = false,
          newItemByLinkError = null,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show error when wishlist fetch fails`() = runTest {
    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.failure(RuntimeException()))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(emptyList()))

    viewModel.uiState.test {
      assertThat(awaitItem()).isEqualTo(
        WishlistDetailUiState.Loading(wishlistName)
      )

      val state = awaitItem()
      assertThat(state).isEqualTo(
        WishlistDetailUiState.Error(wishlistName)
      )
    }
  }

  @Test
  fun `refresh wishlist and items when wishlist edited successfully`() = runTest {
    val wishlist = ownWishlist()
    val items = listOf(wishlistItem(id = "1"))

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(
      Result.success(wishlist),
      Result.success(wishlist),
    )
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(
      Result.success(items),
      Result.success(items),
    )

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onEditWishlistResult(true)

      assertThat(awaitItem()).isEqualTo(
        WishlistDetailUiState.Loading(wishlistName)
      )

      val state = awaitItem()
      assertThat(state).isInstanceOf(WishlistDetailUiState.Listing::class.java)
    }
  }

  @Test
  fun `refresh items when new item result is success`() = runTest {
    val wishlist = ownWishlist()
    val initialItems = listOf(wishlistItem(id = "1"))
    val updatedItems = listOf(wishlistItem(id = "1"), wishlistItem(id = "2"))

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(
      Result.success(initialItems),
      Result.success(updatedItems),
    )

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onNewItemResult(true)

      assertThat(awaitItem()).isEqualTo(
        WishlistDetailUiState.Loading(wishlistName)
      )

      val state = awaitItem()
      assertThat((state as WishlistDetailUiState.Listing).items).hasSize(2)
    }
  }

  @Test
  fun `reopen item detail when edit item result is false`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))

    viewModel.uiState.test {
      awaitItem()
      // awaitItem()

      viewModel.onItemAction(item, WishlistItemAction.Open)
      //awaitItem()

      viewModel.onEditItemResult(false)

      val state = awaitItem()
      assertThat((state as WishlistDetailUiState.Listing).isItemDetailModalOpen).isTrue()
    }
  }

  @Test
  fun `reload selected item when edit item result is true`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")
    val updatedItem = wishlistItem(id = "1", name = "Updated")

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))
    whenever(fetchWishlistItemUseCase(wishlistId, item.id)).thenReturn(Result.success(updatedItem))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onItemAction(item, WishlistItemAction.Open)
      awaitItem()

      viewModel.onEditItemResult(true)

      val loadingState = awaitItem()
      assertThat((loadingState as WishlistDetailUiState.Listing).isLoading).isTrue()

      val state = awaitItem() as WishlistDetailUiState.Listing
      assertThat(state.isItemDetailModalOpen).isTrue()
      assertThat(state.itemSelected).isEqualTo(updatedItem)
    }
  }

  @Test
  fun `delete wishlist and emit side effect when success`() = runTest {
    val wishlist = ownWishlist()
    val items = listOf(wishlistItem(id = "1"))

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(items))
    whenever(deleteWishlistUseCase(wishlist)).thenReturn(Result.success(Unit))

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      uiStateTurbine.awaitItem()
      uiStateTurbine.awaitItem()

      viewModel.onDeleteWishlist(wishlist)

      val loadingState = uiStateTurbine.awaitItem() as WishlistDetailUiState.Listing
      assertThat(loadingState.isLoading).isTrue()

      val finalState = uiStateTurbine.awaitItem() as WishlistDetailUiState.Listing
      assertThat(finalState.isLoading).isFalse()

      assertThat(sideEffectTurbine.awaitItem()).isEqualTo(
        WishlistDetailUiSideEffect.WishlistDeleted
      )

      uiStateTurbine.cancelAndIgnoreRemainingEvents()
      sideEffectTurbine.cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `show error when delete wishlist fails`() = runTest {
    val wishlist = ownWishlist()
    val items = listOf(wishlistItem(id = "1"))
    val error = RuntimeException()

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(items))
    whenever(deleteWishlistUseCase(wishlist)).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onDeleteWishlist(wishlist)

      awaitItem()

      val state = awaitItem() as WishlistDetailUiState.Listing
      assertThat(state.isLoading).isFalse()
      assertThat(state.error).isEqualTo(errorUiModel())
    }
  }

  @Test
  fun `open item detail`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onItemAction(item, WishlistItemAction.Open)

      val state = awaitItem() as WishlistDetailUiState.Listing
      assertThat(state.isItemDetailModalOpen).isTrue()
      assertThat(state.itemSelected).isEqualTo(item)
    }
  }

  @Test
  fun `close item detail modal`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onItemAction(item, WishlistItemAction.Open)
      awaitItem()

      viewModel.onCloseItemDetailModal()

      val state = awaitItem() as WishlistDetailUiState.Listing
      assertThat(state.isItemDetailModalOpen).isFalse()
      assertThat(state.itemSelected).isNull()
    }
  }

  @Test
  fun `delete item and remove it from list when success`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))
    whenever(deleteWishlistItemUseCase(wishlistId, item.id)).thenReturn(Result.success(Unit))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onItemAction(item, WishlistItemAction.Open)
      awaitItem()

      viewModel.onItemAction(item, WishlistItemAction.Delete)

      val loadingState = awaitItem() as WishlistDetailUiState.Listing
      assertThat(loadingState.isLoading).isTrue()

      val state = awaitItem()
      assertThat(state).isEqualTo(
        WishlistDetailUiState.Empty(
          wishlistName = wishlistName,
          wishlist = wishlist,
          isNewItemByLinkModalOpen = false,
          newItemByLinkError = null,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `emit nav to edit side effect when edit item action is selected`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      uiStateTurbine.awaitItem()

      viewModel.onItemAction(item, WishlistItemAction.Edit)

      val state = uiStateTurbine.awaitItem() as WishlistDetailUiState.Listing
      assertThat(state.isItemDetailModalOpen).isFalse()

      assertThat(sideEffectTurbine.awaitItem()).isEqualTo(
        WishlistDetailUiSideEffect.NavToEdit(item.id)
      )

      uiStateTurbine.cancelAndIgnoreRemainingEvents()
      sideEffectTurbine.cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `toggle purchase and update item when success`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")
    val updatedItem = wishlistItem(
      id = "1",
      purchased = PurchaseMetadata(
        purchasedBy = User.Basic("", "", "", null),
        purchasedAt = Date(1L)
      )
    )

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))
    whenever(updateWishlistItemPurchaseUseCase(wishlistId, item)).thenReturn(Result.success(Unit))
    whenever(fetchWishlistItemUseCase(wishlistId, item.id)).thenReturn(Result.success(updatedItem))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onItemAction(item, WishlistItemAction.TogglePurchase)

      val loadingState = awaitItem() as WishlistDetailUiState.Listing
      assertThat(loadingState.isItemDetailButtonLoading).isTrue()

      val state = awaitItem() as WishlistDetailUiState.Listing
      assertThat(state.isItemDetailButtonLoading).isFalse()
      assertThat(state.items).contains(updatedItem)
    }
  }

  @Test
  fun `show error when toggle purchase fails`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")
    val error = RuntimeException()

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))
    whenever(updateWishlistItemPurchaseUseCase(wishlistId, item)).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onItemAction(item, WishlistItemAction.TogglePurchase)

      val loadingState = awaitItem() as WishlistDetailUiState.Listing
      assertThat(loadingState.isItemDetailButtonLoading).isTrue()

      val state = awaitItem() as WishlistDetailUiState.Listing
      assertThat(state.isItemDetailButtonLoading).isFalse()
      assertThat(state.error).isEqualTo(errorUiModel())
    }
  }

  @Test
  fun `change new item by link modal visibility`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onChangeItemByLinkModalVisibility(true)

      val state = awaitItem() as WishlistDetailUiState.Listing
      assertThat(state.isNewItemByLinkModalOpen).isTrue()
      assertThat(state.newItemByLinkError).isNull()
    }
  }

  @Test
  fun `clear link input error`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")
    val error = RuntimeException()

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))
    whenever(updateWishlistItemPurchaseUseCase(wishlistId, item)).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()

      viewModel.onChangeItemByLinkModalVisibility(true)

      viewModel.onClearInputError(WishlistItemForm.Input.Link)

      val state = awaitItem() as WishlistDetailUiState.Listing
      assertThat(state.newItemByLinkError).isNull()
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val wishlist = ownWishlist()
    val item = wishlistItem(id = "1")
    val error = RuntimeException()

    whenever(fetchWishlistUseCase(wishlistId)).thenReturn(Result.success(wishlist))
    whenever(fetchWishlistItemsUseCase(wishlistId)).thenReturn(Result.success(listOf(item)))
    whenever(deleteWishlistUseCase(wishlist)).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onDeleteWishlist(wishlist)
      awaitItem()
      awaitItem()

      viewModel.onDismissError()

      val state = awaitItem() as WishlistDetailUiState.Listing
      assertThat(state.error).isNull()
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

  private fun wishlistItem(
    id: String = "",
    photoUrl: String? = null,
    name: String = "",
    description: String = "",
    store: String = "",
    unitPrice: Float = 2f,
    amount: Int = 1,
    priority: Priority = Priority.Standard,
    link: String = "",
    tags: List<String> = emptyList(),
    createdBy: User.Basic = User.Basic("", "", "", null),
    createdAt: Date = Date(1L),
    lastUpdate: WishlistItem.UpdateMetadata = WishlistItem.UpdateMetadata(
      User.Basic("", "", "", null),
      updatedAt = Date(1L)
    ),
    purchased: PurchaseMetadata? = null
  ): WishlistItem =
    WishlistItem(
      id = id,
      photoUrl = photoUrl,
      name = name,
      description = description,
      store = store,
      unitPrice = unitPrice,
      amount = amount,
      priority = priority,
      link = link,
      tags = tags,
      createdBy = createdBy,
      createdAt = createdAt,
      lastUpdate = lastUpdate,
      purchased = purchased,
    )
}
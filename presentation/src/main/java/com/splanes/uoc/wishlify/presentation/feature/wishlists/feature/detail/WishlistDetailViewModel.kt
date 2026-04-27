package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.UpdateWishlistItemPurchaseUseCase
import com.splanes.uoc.wishlify.presentation.common.components.filters.FilterProduct
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemForm
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates the wishlist detail flow, including item actions, filters and wishlist deletion.
 */
class WishlistDetailViewModel(
  private val wishlistId: String,
  wishlistName: String,
  private val fetchWishlistUseCase: FetchWishlistUseCase,
  private val fetchWishlistItemsUseCase: FetchWishlistItemsUseCase,
  private val fetchWishlistItemUseCase: FetchWishlistItemUseCase,
  private val deleteWishlistUseCase: DeleteWishlistUseCase,
  private val deleteWishlistItemUseCase: DeleteWishlistItemUseCase,
  private val updateWishlistItemPurchaseUseCase: UpdateWishlistItemPurchaseUseCase,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState(wishlistId, wishlistName))

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchWishlistAndItems(wishlistId) }
    .map { state ->
      state.toUiState(
        errorUiMapper = errorUiMapper,
      )
    }
    .stateIn(
      initialValue = viewModelState.value.toUiState(
        errorUiMapper = errorUiMapper,
      ),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<WishlistDetailUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  /**
   * Reloads the wishlist after returning from the wishlist edition flow.
   */
  fun onEditWishlistResult(updated: Boolean) {
    if (updated) {
      viewModelScope.launch {
        fetchWishlistAndItems(wishlistId)
      }
    }
  }

  /**
   * Refreshes the items list after returning from item creation.
   */
  fun onNewItemResult(created: Boolean) {
    if (created) {
      fetchItems()
    }
  }

  /**
   * Refreshes the selected item after returning from item edition, keeping the detail modal in
   * sync when needed.
   */
  fun onEditItemResult(updated: Boolean) {
    if (updated) {
      val item = viewModelState.value.itemSelected
      if (item != null) {
        viewModelState.update { state -> state.copy(isLoading = true) }
        viewModelScope.launch {
          val result = fetchWishlistItemUseCase(wishlistId, item.id)
          viewModelState.update { state ->
            val itemUpdated = result.getOrNull()
            state.copy(
              isLoading = false,
              isItemDetailModalOpen = itemUpdated != null,
              itemSelected = itemUpdated,
              items = if (itemUpdated != null) {
                (state.items - item) + itemUpdated
              } else {
                state.items
              }
            )
          }
        }
      }
    } else {
      viewModelState.update { state ->
        state.copy(isItemDetailModalOpen = true)
      }
    }
  }

  /**
   * Deletes the current wishlist.
   */
  fun onDeleteWishlist(wishlist: Wishlist) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      deleteWishlistUseCase(wishlist)
        .onSuccess {
          viewModelState.update { state -> state.copy(isLoading = false) }
          uiSideEffectChannel.send(WishlistDetailUiSideEffect.WishlistDeleted)
        }
        .onFailure { error ->
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              error = error
            )
          }
        }
    }
  }

  /**
   * Dispatches an item interaction to the matching detail action.
   */
  fun onItemAction(item: WishlistItem, action: WishlistItemAction) {
    when (action) {
      WishlistItemAction.Open -> onOpenItem(item)
      WishlistItemAction.Delete -> onDeleteItem(item)
      WishlistItemAction.Edit -> onEditItem(item)
      WishlistItemAction.TogglePurchase -> onTogglePurchase(item)
      WishlistItemAction.OpenLink -> error("Open link on VM, this should not happen")
    }
  }

  /**
   * Closes the item detail modal.
   */
  fun onCloseItemDetailModal() {
    viewModelState.update { state ->
      state.copy(
        isItemDetailModalOpen = false,
        itemSelected = null
      )
    }
  }

  /**
   * Updates the active product filters used to derive the visible items list.
   */
  fun onChangeProductFilters(filters: List<FilterProduct>) {
    viewModelState.update { state ->
      state.copy(filters = filters)
    }
  }

  /**
   * Opens or closes the modal used to create a new item from a link.
   */
  fun onChangeItemByLinkModalVisibility(visible: Boolean) {
    viewModelState.update { state ->
      state.copy(
        isNewItemByLinkModalOpen = visible,
        newItemByLinkError = null
      )
    }
  }

  /**
   * Clears the validation error associated with a specific item form input.
   */
  fun onClearInputError(input: WishlistItemForm.Input) {
    viewModelState.update { state ->
      when (input) {
        WishlistItemForm.Input.Link -> state.copy(newItemByLinkError = null)
        else -> state
      }
    }
  }

  /**
   * Clears the current UI error.
   */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /**
   * Loads the wishlist header and its items in parallel.
   */
  private suspend fun fetchWishlistAndItems(wishlistId: String) {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }

    coroutineScope {
      val wishlistDeferred = async { fetchWishlistUseCase(wishlistId) }
      val itemsDeferred = async { fetchWishlistItemsUseCase(wishlistId) }

      val wishlistResult = wishlistDeferred.await()
      val itemsResult = itemsDeferred.await()

      viewModelState.update { state ->
        state.copy(
          isLoadingFullscreen = false,
          wishlist = wishlistResult.getOrNull(),
          items = itemsResult.getOrDefault(emptyList()),
          error = itemsResult.exceptionOrNull()
        )
      }
    }
  }

  /**
   * Reloads only the wishlist items list.
   */
  private fun fetchItems() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    viewModelScope.launch {
      val result = fetchWishlistItemsUseCase(wishlistId)
      viewModelState.update { state ->
        state.copy(
          isLoadingFullscreen = false,
          items = result.getOrDefault(emptyList()),
          error = result.exceptionOrNull()
        )
      }
    }
  }

  /**
   * Opens the item detail modal for the selected item.
   */
  private fun onOpenItem(item: WishlistItem) {
    viewModelState.update { state ->
      state.copy(
        itemSelected = item,
        isItemDetailModalOpen = true
      )
    }
  }

  /**
   * Deletes the selected wishlist item.
   */
  private fun onDeleteItem(item: WishlistItem) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      deleteWishlistItemUseCase(wishlist = wishlistId, item = item.id)
        .onSuccess {
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              isItemDetailModalOpen = false,
              items = state.items - item
            )
          }
        }
        .onFailure { error ->
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              error = error
            )
          }
        }
    }
  }

  /**
   * Requests navigation to the item edition flow.
   */
  private fun onEditItem(item: WishlistItem) {
    viewModelState.update { state -> state.copy(isItemDetailModalOpen = false) }
    uiSideEffectChannel.trySend(WishlistDetailUiSideEffect.NavToEdit(item.id))
  }

  /**
   * Toggles the purchase state of the selected item and refreshes its latest persisted snapshot.
   */
  private fun onTogglePurchase(item: WishlistItem) {
    viewModelState.update { state -> state.copy(isItemDetailButtonLoading = true) }
    viewModelScope.launch {
      updateWishlistItemPurchaseUseCase(wishlistId = wishlistId, item = item)
        .mapCatching { fetchWishlistItemUseCase(wishlistId, item = item.id).getOrThrow() }
        .onSuccess { itemUpdated ->
          viewModelState.update { state ->
            state.copy(
              items = (state.items - item) + itemUpdated,
              itemSelected = itemUpdated.takeIf { state.isItemDetailModalOpen },
              isItemDetailButtonLoading = false
            )
          }
        }
        .onFailure { error ->
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              error = error,
              isItemDetailButtonLoading = false
            )
          }
        }
    }
  }

  private data class ViewModelState(
    val wishlistId: String,
    val wishlistName: String,
    val wishlist: Wishlist? = null,
    val items: List<WishlistItem> = emptyList(),
    val filters: List<FilterProduct> = emptyList(),
    val isItemDetailModalOpen: Boolean = false,
    val isItemDetailButtonLoading: Boolean = false,
    val itemSelected: WishlistItem? = null,
    val isNewItemByLinkModalOpen: Boolean = false,
    val newItemByLinkError: String? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    /**
     * Maps internal state to the wishlist detail UI contract.
     */
    fun toUiState(
      errorUiMapper: ErrorUiMapper
    ): WishlistDetailUiState =
      when {
        isLoadingFullscreen ->
          WishlistDetailUiState.Loading(wishlistName = wishlistName)

        wishlist == null ->
          WishlistDetailUiState.Error(wishlistName = wishlistName)

        items.isEmpty(filters) ->
          WishlistDetailUiState.Empty(
            wishlistName = wishlistName,
            wishlist = wishlist,
            productFilters = filters,
            isNewItemByLinkModalOpen = isNewItemByLinkModalOpen,
            newItemByLinkError = newItemByLinkError,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )

        else ->
          WishlistDetailUiState.Listing(
            wishlistName = wishlistName,
            wishlist = wishlist,
            productFilters = filters,
            items = items.sorted().applyFilters(filters),
            itemSelected = itemSelected,
            isItemDetailModalOpen = isItemDetailModalOpen,
            isItemDetailButtonLoading = isItemDetailButtonLoading,
            isNewItemByLinkModalOpen = isNewItemByLinkModalOpen,
            newItemByLinkError = newItemByLinkError,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }

    /**
     * Indicates whether the current filtered list has visible items.
     */
    private fun List<WishlistItem>.isEmpty(filters: List<FilterProduct>) =
      isEmpty() || applyFilters(filters).isEmpty()

    /**
     * Applies the product filters configured from the detail screen.
     */
    private fun List<WishlistItem>.applyFilters(filters: List<FilterProduct>) =
      if (filters.isEmpty()) {
        this
      } else {
        this
          .filter { item ->
            filters
              .filterIsInstance<FilterProduct.Price>()
              .all { filter ->
                when (filter.value) {
                  is FilterProduct.EqualTo<*> -> item.price == filter.value.value
                  is FilterProduct.GreaterThan<*> -> item.price > filter.value.value
                  is FilterProduct.LessThan<*> -> item.price < filter.value.value
                }
              }
          }
          .filter { item ->
            filters
              .filterIsInstance<FilterProduct.Priority>()
              .all { filter ->
                when (filter.value) {
                  is FilterProduct.EqualTo<*> -> item.priority == filter.value.value
                  is FilterProduct.GreaterThan<*> -> item.priority.weight > filter.value.value.weight
                  is FilterProduct.LessThan<*> -> item.priority.weight < filter.value.value.weight
                }
              }
          }
      }

    /**
     * Sorts items by purchase status, priority and creation time.
     */
    private fun List<WishlistItem>.sorted() = sortedWith(
      compareBy<WishlistItem> { it.purchased != null }
        .thenByDescending { it.priority.weight }
        .thenBy { it.createdAt }
    )
  }
}

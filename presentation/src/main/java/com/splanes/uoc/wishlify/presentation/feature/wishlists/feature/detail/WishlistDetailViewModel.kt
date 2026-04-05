package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.UpdateWishlistItemPurchaseUseCase
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

class WishlistDetailViewModel(
  private val wishlistId: String,
  wishlistName: String,
  private val fetchWishlistUseCase: FetchWishlistUseCase,
  private val fetchWishlistItemsUseCase: FetchWishlistItemsUseCase,
  private val fetchWishlistItemUseCase: FetchWishlistItemUseCase,
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

  fun onNewItemResult(created: Boolean) {
    if (created) {
      fetchItems()
    }
  }

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

  fun onItemAction(item: WishlistItem, action: WishlistItemAction) {
    when (action) {
      WishlistItemAction.Open -> onOpenItem(item)
      WishlistItemAction.Delete -> onDeleteItem(item)
      WishlistItemAction.Edit -> onEditItem(item)
      WishlistItemAction.TogglePurchase -> onTogglePurchase(item)
      WishlistItemAction.OpenLink -> error("Open link on VM, this should not happen")
    }
  }

  fun onCloseItemDetailModal() {
    viewModelState.update { state ->
      state.copy(
        isItemDetailModalOpen = false,
        itemSelected = null
      )
    }
  }

  fun onChangeItemByLinkModalVisibility(visible: Boolean) {
    viewModelState.update { state ->
      state.copy(
        isNewItemByLinkModalOpen = visible,
        newItemByLinkError = null
      )
    }
  }

  fun onClearInputError(input: WishlistItemForm.Input) {
    viewModelState.update { state ->
      when (input) {
        WishlistItemForm.Input.Link -> state.copy(newItemByLinkError = null)
        else -> state
      }
    }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

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

  private fun onOpenItem(item: WishlistItem) {
    viewModelState.update { state ->
      state.copy(
        itemSelected = item,
        isItemDetailModalOpen = true
      )
    }
  }

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

  private fun onEditItem(item: WishlistItem) {
    viewModelState.update { state -> state.copy(isItemDetailModalOpen = false) }
    uiSideEffectChannel.trySend(WishlistDetailUiSideEffect.NavToEdit(item.id))
  }

  private fun onTogglePurchase(item: WishlistItem) {
    viewModelState.update { state -> state.copy(isItemDetailButtonLoading = true) }
    viewModelScope.launch {
      updateWishlistItemPurchaseUseCase(wishlistId = wishlistId, item = item)
        .mapCatching { fetchWishlistItemUseCase(wishlistId, item = item.id).getOrThrow() }
        .onSuccess { itemUpdated ->
          viewModelState.update { state ->
            state.copy(
              items = (state.items - item) + itemUpdated,
              isItemDetailModalOpen = true,
              itemSelected = itemUpdated,
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
    val isItemDetailModalOpen: Boolean = false,
    val isItemDetailButtonLoading: Boolean = false,
    val itemSelected: WishlistItem? = null,
    val isNewItemByLinkModalOpen: Boolean = false,
    val newItemByLinkError: String? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    fun toUiState(
      errorUiMapper: ErrorUiMapper
    ): WishlistDetailUiState =
      when {
        isLoadingFullscreen ->
          WishlistDetailUiState.Loading(wishlistName = wishlistName)

        wishlist == null ->
          WishlistDetailUiState.Error(wishlistName = wishlistName)

        items.isEmpty() ->
          WishlistDetailUiState.Empty(
            wishlistName = wishlistName,
            wishlist = wishlist,
            isNewItemByLinkModalOpen = isNewItemByLinkModalOpen,
            newItemByLinkError = newItemByLinkError,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )

        else ->
          WishlistDetailUiState.Listing(
            wishlistName = wishlistName,
            wishlist = wishlist,
            items = items,
            itemSelected = itemSelected,
            isItemDetailModalOpen = isItemDetailModalOpen,
            isItemDetailButtonLoading = isItemDetailButtonLoading,
            isNewItemByLinkModalOpen = isNewItemByLinkModalOpen,
            newItemByLinkError = newItemByLinkError,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }
  }
}
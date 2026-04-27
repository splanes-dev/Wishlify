package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.SubscribeSharedWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.UpdateSharedWishlistItemUseCase
import com.splanes.uoc.wishlify.presentation.common.components.filters.FilterProduct
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.mapper.SharedWishlistItemStateErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.mapper.SharedWishlistItemUiMapper
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.model.SharedWishlistItemAction
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.model.SharedWishlistItemStateRequestError
import com.splanes.uoc.wishlify.presentation.feature.shared.model.SharedWishlistItemStateAction
import com.splanes.uoc.wishlify.presentation.feature.shared.model.SharedWishlistState
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates the third-party shared wishlist detail flow, including real-time item updates,
 * item state transitions and filtering.
 */
class SharedWishlistThirdPartyDetailViewModel(
  sharedWishlistName: String,
  target: String,
  private val sharedWishlistId: String,
  private val fetchSharedWishlistUseCase: FetchSharedWishlistUseCase,
  private val fetchSharedWishlistItemsUseCase: FetchSharedWishlistItemsUseCase,
  private val fetchSharedWishlistItemUseCase: FetchSharedWishlistItemUseCase,
  private val updateSharedWishlistItemUseCase: UpdateSharedWishlistItemUseCase,
  private val itemUiMapper: SharedWishlistItemUiMapper,
  private val itemStateErrorMapper: SharedWishlistItemStateErrorMapper,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState(sharedWishlistName, target))

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchSharedWishlistAndItems(sharedWishlistId) }
    .map { state -> state.toUiState(itemStateErrorMapper, errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(itemStateErrorMapper, errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )


  /**
   * Dispatches an item interaction to the corresponding detail or state-update flow.
   */
  fun onItemAction(item: SharedWishlistItem, action: SharedWishlistItemAction) {
    when (action) {
      SharedWishlistItemAction.Open -> onOpenDetail(item)
      is SharedWishlistItemAction.UpdateState -> onUpdateState(item, action)
      else /* Open link, should not be handled here */ -> {
        // Nothing to do
      }
    }
  }

  /**
   * Opens the bottom sheet used to update the selected item state.
   */
  fun onOpenItemStateModal(item: SharedWishlistItem) {
    viewModelState.update { state ->
      state.copy(
        itemSelectedToUpdateState = item,
        isWishlistItemStateModalOpen = true
      )
    }
  }

  /**
   * Closes the currently open item detail modal.
   */
  fun onCloseItemDetailModal() {
    viewModelState.update { state ->
      state.copy(
        itemSelected = null,
        itemStateActions = emptyList(),
        isItemDetailModalOpen = false,
        shareRequestError = null
      )
    }
  }

  /**
   * Closes the item state modal and clears any share request validation error.
   */
  fun onCloseItemStateModal() {
    viewModelState.update { state ->
      state.copy(
        itemSelectedToUpdateState = null,
        isWishlistItemStateModalOpen = false,
        shareRequestError = null,
      )
    }
  }

  /**
   * Clears the validation error associated with the share request participants input.
   */
  fun onClearShareRequestError() {
    viewModelState.update { state -> state.copy(shareRequestError = null) }
  }

  /**
   * Updates the selected item state using the item-state modal flow.
   */
  fun onUpdateItemState(item: SharedWishlistItem, action: SharedWishlistItemAction.UpdateState) {
    val wishlist = viewModelState.value.sharedWishlist

    if (wishlist == null) {
      viewModelState.update { state -> state.copy(error = GenericError.Unknown()) }
      return
    }

    // -1 'cause the current user doesn't count
    val maxNumOfParticipants = wishlist.totalParticipantsCount() - 1

    if (validateForm(action, maxNumOfParticipants)) {
      val request = itemUiMapper.updateRequestOf(wishlist, item, action)

      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        val result = updateSharedWishlistItemUseCase(request)
        result
          .mapCatching {
            fetchSharedWishlistItemUseCase(
              sharedWishlistId = wishlist.id,
              sharedWishlistItemId = item.id
            ).getOrThrow()
          }
          .onSuccess { itemUpdated ->
            viewModelState.update { state ->
              state.copy(
                items = (state.items - item) + itemUpdated,
                itemSelectedToUpdateState = null,
                isWishlistItemStateModalOpen = false,
                isLoading = false,
              )
            }
          }
          .onFailure { error ->
            viewModelState.update { state ->
              state.copy(
                itemSelectedToUpdateState = null,
                isWishlistItemStateModalOpen = false,
                isLoading = false,
                error = error
              )
            }
        }
      }
    }
  }

  /**
   * Updates the active product filters used to derive the visible items list.
   */
  fun onChangeProductFilters(filters: List<FilterProduct>) {
    viewModelState.update { state -> state.copy(filters = filters) }
  }

  /**
   * Hides the informational banner shown at the top of the wishlist.
   */
  fun onDismissBanner() {
    viewModelState.update { state -> state.copy(isInfoBannerVisible = false) }
  }

  /**
   * Clears the current UI error.
   */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /**
   * Opens the item detail modal and resolves the actions available for the selected item state.
   */
  private fun onOpenDetail(item: SharedWishlistItem) {
    val wishlist = viewModelState.value.sharedWishlist
    val actions = wishlist?.let {
      buildSelectedItemStateActions(wishlist, currentState = item.state)
    } ?: emptyList()

    viewModelState.update { state ->
      state.copy(
        itemSelected = item,
        itemStateActions = actions,
        isItemDetailModalOpen = true
      )
    }
  }

  /**
   * Updates the selected item state directly from the item detail modal.
   */
  private fun onUpdateState(
    item: SharedWishlistItem,
    action: SharedWishlistItemAction.UpdateState
  ) {
    val wishlist = viewModelState.value.sharedWishlist

    if (wishlist == null) {
      viewModelState.update { state -> state.copy(error = GenericError.Unknown()) }
      return
    }

    // -1 'cause the current user doesn't count
    val maxNumOfParticipants = wishlist.totalParticipantsCount() - 1

    if (validateForm(action, maxNumOfParticipants)) {
      val request = itemUiMapper.updateRequestOf(wishlist, item, action)

      viewModelState.update { state ->
        state.copy(
          isItemDetailButtonLoading = true,
          isLoading = true
        )
      }
      viewModelScope.launch {
        val result = updateSharedWishlistItemUseCase(request)
        result
          .mapCatching {
            fetchSharedWishlistItemUseCase(
              sharedWishlistId = wishlist.id,
              sharedWishlistItemId = item.id
            ).getOrThrow()
          }
          .onSuccess { itemUpdated ->
            viewModelState.update { state ->
              state.copy(
                items = (state.items - item) + itemUpdated,
                itemSelected = itemUpdated,
                itemStateActions = buildSelectedItemStateActions(wishlist, itemUpdated.state),
                isItemDetailButtonLoading = false,
                isLoading = false,
              )
            }
          }
          .onFailure { error ->
            viewModelState.update { state ->
              state.copy(
                isItemDetailButtonLoading = false,
                isLoading = false,
                error = error
              )
            }
          }
      }
    }
  }

  /**
   * Loads the shared wishlist header and starts observing its items in real time.
   */
  private suspend fun fetchSharedWishlistAndItems(id: String) {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    coroutineScope {
      val wishlistDeferred = async { fetchSharedWishlistUseCase(id) }
      val itemsDeferred = async { fetchSharedWishlistItemsUseCase(id) }
      val wishlist = wishlistDeferred.await()
      val items = itemsDeferred.await()
      viewModelState.update { state ->
        state.copy(
          sharedWishlist = wishlist.getOrNull() as? SharedWishlist.ThirdParty,
          items = items.getOrDefault(emptyList()),
          isLoadingFullscreen = false
        )
      }
    }
  }

  /**
   * Resolves the item state actions allowed for the current viewer and item state.
   */
  private fun buildSelectedItemStateActions(
    wishlist: SharedWishlist.ThirdParty,
    currentState: SharedWishlistItem.State
  ): List<SharedWishlistItemStateAction> = buildList {
    when (currentState) {
      SharedWishlistItem.Available -> {
        add(SharedWishlistItemStateAction.Purchase)
        add(SharedWishlistItemStateAction.Lock)
        if (wishlist.totalParticipantsCount() > 1) { // There is more ppl than the current user
          add(SharedWishlistItemStateAction.RequestShare)
        }
      }

      is SharedWishlistItem.Lock -> {
        if (currentState.isCurrentUserParticipant) {
          add(SharedWishlistItemStateAction.Purchase)
          if (currentState.isLockedByCurrentUser) {
            add(SharedWishlistItemStateAction.Unlock)
          }
        }
      }

      is SharedWishlistItem.ShareRequest -> {
        if (currentState.isCurrentUserParticipant) {
          add(SharedWishlistItemStateAction.Purchase)
          if (currentState.isRequestedByCurrentUser && currentState.participantsJoined.count() == 0) {
            add(SharedWishlistItemStateAction.CancelShareRequest)
          }
        } else {
          add(SharedWishlistItemStateAction.JoinToShareRequest)
        }
      }

      is SharedWishlistItem.Purchased -> {
        // No action allowed if it's already purchased
      }
    }
  }

  /**
   * Validates additional inputs required by certain state transitions, such as share requests.
   */
  private fun validateForm(action: SharedWishlistItemAction, maxNumOfParticipants: Int): Boolean =
    when (action) {
      is SharedWishlistItemAction.ShareRequest -> {
        val error = when {
          action.numOfParticipants !in 1..maxNumOfParticipants ->
            SharedWishlistItemStateRequestError.ShareRequestInvalid(maxNumOfParticipants)

          else -> null
        }

        viewModelState.update { state -> state.copy(shareRequestError = error) }

        error == null
      }

      else -> true
    }

  private data class ViewModelState(
    val sharedWishlistName: String,
    val target: String,
    val sharedWishlist: SharedWishlist.ThirdParty? = null,
    val items: List<SharedWishlistItem> = emptyList(),
    val filters: List<FilterProduct> = emptyList(),
    val itemSelected: SharedWishlistItem? = null,
    val itemSelectedToUpdateState: SharedWishlistItem? = null,
    val itemStateActions: List<SharedWishlistItemStateAction> = emptyList(),
    val isInfoBannerVisible: Boolean = true,
    val isItemDetailModalOpen: Boolean = false,
    val isItemDetailButtonLoading: Boolean = false,
    val isWishlistItemStateModalOpen: Boolean = false,
    val shareRequestError: SharedWishlistItemStateRequestError? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    /**
     * Maps internal state to the detail UI contract.
     */
    fun toUiState(
      itemStateErrorMapper: SharedWishlistItemStateErrorMapper,
      errorUiMapper: ErrorUiMapper
    ) = when {
      isLoadingFullscreen ->
        SharedWishlistThirdPartyDetailUiState.Loading(sharedWishlistName, target)

      sharedWishlist != null && items.isNotEmpty() ->
        SharedWishlistThirdPartyDetailUiState.Listing(
          wishlistName = sharedWishlistName,
          target = target,
          wishlist = sharedWishlist,
          isInfoBannerVisible = isInfoBannerVisible,
          isItemDetailModalOpen = isItemDetailModalOpen,
          isItemDetailButtonLoading = isItemDetailButtonLoading,
          itemSelected = itemSelected,
          itemSelectedToUpdateState = itemSelectedToUpdateState,
          isWishlistItemStateModalOpen = isWishlistItemStateModalOpen,
          itemStateActions = itemStateActions,
          items = items.sorted().applyFilters(filters),
          productFilters = filters,
          shareRequestError = shareRequestError?.let(itemStateErrorMapper::map),
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )

      else ->
        SharedWishlistThirdPartyDetailUiState.Error(sharedWishlistName, target)
    }

    /**
     * Sorts items by collaborative state and prioritises those involving the current user.
     */
    private fun List<SharedWishlistItem>.sorted() = sortedWith(
      compareBy<SharedWishlistItem> { it.state }
        .thenByDescending { it.state.isCurrentUserParticipant }
    )

    /**
     * Applies the product filters configured from the detail screen.
     */
    private fun List<SharedWishlistItem>.applyFilters(filters: List<FilterProduct>) =
      if (filters.isEmpty()) {
        this
      } else {
        this
          .filter { item ->
            filters
              .filterIsInstance<FilterProduct.Price>()
              .all { filter ->
                when (filter.value) {
                  is FilterProduct.EqualTo<*> -> item.linkedItem.price == filter.value.value
                  is FilterProduct.GreaterThan<*> -> item.linkedItem.price > filter.value.value
                  is FilterProduct.LessThan<*> -> item.linkedItem.price < filter.value.value
                }
              }
          }
          .filter { item ->
            filters
              .filterIsInstance<FilterProduct.Priority>()
              .all { filter ->
                when (filter.value) {
                  is FilterProduct.EqualTo<*> -> item.linkedItem.priority == filter.value.value
                  is FilterProduct.GreaterThan<*> -> item.linkedItem.priority.weight > filter.value.value.weight
                  is FilterProduct.LessThan<*> -> item.linkedItem.priority.weight < filter.value.value.weight
                }
              }
          }
          .filter { item ->
            filters
              .filterIsInstance<FilterProduct.ProductState>()
              .any { filter ->
                val value = filter.value.value
                when (value) {
                  SharedWishlistState.Purchase -> item.state is SharedWishlistItem.Purchased
                  SharedWishlistState.Lock -> item.state is SharedWishlistItem.Lock
                  SharedWishlistState.RequestShare -> item.state is SharedWishlistItem.ShareRequest
                  SharedWishlistState.Available -> item.state is SharedWishlistItem.Available
                }
              }
          }
      }
  }
}

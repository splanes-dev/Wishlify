package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.UpdateSharedWishlistItemUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.mapper.SharedWishlistItemStateErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.mapper.SharedWishlistItemUiMapper
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.model.SharedWishlistItemAction
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.model.SharedWishlistItemStateRequestError
import com.splanes.uoc.wishlify.presentation.feature.shared.model.SharedWishlistItemStateAction
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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


  fun onItemAction(item: SharedWishlistItem, action: SharedWishlistItemAction) {
    when (action) {
      SharedWishlistItemAction.Open -> onOpenDetail(item)
      is SharedWishlistItemAction.UpdateState -> onUpdateState(item, action)
      else /* Open link, should not be handled here */ -> {
        // Nothing to do
      }
    }
  }

  fun onOpenItemStateModal(item: SharedWishlistItem) {
    viewModelState.update { state ->
      state.copy(
        itemSelectedToUpdateState = item,
        isWishlistItemStateModalOpen = true
      )
    }
  }

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

  fun onCloseItemStateModal() {
    viewModelState.update { state ->
      state.copy(
        itemSelectedToUpdateState = null,
        isWishlistItemStateModalOpen = false,
        shareRequestError = null,
      )
    }
  }

  fun onClearShareRequestError() {
    viewModelState.update { state -> state.copy(shareRequestError = null) }
  }

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

  fun onDismissBanner() {
    viewModelState.update { state -> state.copy(isInfoBannerVisible = false) }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

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
          items = items.sorted(),
          shareRequestError = shareRequestError?.let(itemStateErrorMapper::map),
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )

      else ->
        SharedWishlistThirdPartyDetailUiState.Error(sharedWishlistName, target)
    }

    private fun List<SharedWishlistItem>.sorted() = sortedWith(
      compareBy<SharedWishlistItem> { it.state }
        .thenByDescending { it.state.isCurrentUserParticipant }
    )
  }
}
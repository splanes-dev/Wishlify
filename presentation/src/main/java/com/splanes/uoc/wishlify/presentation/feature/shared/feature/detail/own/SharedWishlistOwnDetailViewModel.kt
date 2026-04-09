package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.WishlistDetailUiSideEffect
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

class SharedWishlistOwnDetailViewModel(
  private val sharedWishlistId: String,
  sharedWishlistName: String,
  target: String,
  private val fetchSharedWishlistUseCase: FetchSharedWishlistUseCase,
  private val fetchSharedWishlistItemsUseCase: FetchSharedWishlistItemsUseCase,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow(ViewModelState(sharedWishlistId, sharedWishlistName, target))

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchWishlistAndItems(sharedWishlistId) }
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

  fun onOpenItemDetail(item: SharedWishlistItem) {
    viewModelState.update { state ->
      state.copy(
        itemSelected = item,
        isItemDetailModalOpen = true
      )
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

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private suspend fun fetchWishlistAndItems(wishlistId: String) {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }

    coroutineScope {
      val wishlistDeferred = async { fetchSharedWishlistUseCase(wishlistId) }
      val itemsDeferred = async { fetchSharedWishlistItemsUseCase(wishlistId) }

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

  private data class ViewModelState(
    val sharedWishlistId: String,
    val wishlistName: String,
    val wishlistTarget: String,
    val wishlist: SharedWishlist? = null,
    val items: List<SharedWishlistItem> = emptyList(),
    val isItemDetailModalOpen: Boolean = false,
    val itemSelected: SharedWishlistItem? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    fun toUiState(
      errorUiMapper: ErrorUiMapper
    ): SharedWishlistOwnDetailUiState =
      when {
        isLoadingFullscreen ->
          SharedWishlistOwnDetailUiState.Loading(
            wishlistName = wishlistName,
            wishlistTarget = wishlistTarget
          )

        wishlist == null ->
          SharedWishlistOwnDetailUiState.Error(
            wishlistName = wishlistName,
            wishlistTarget = wishlistTarget
          )

        else ->
          SharedWishlistOwnDetailUiState.Listing(
            wishlistName = wishlistName,
            wishlistTarget = wishlistTarget,
            wishlist = wishlist,
            items = items,
            itemSelected = itemSelected,
            isItemDetailModalOpen = isItemDetailModalOpen,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }
  }
}
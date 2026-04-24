package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.UnshareWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistUseCase
import com.splanes.uoc.wishlify.presentation.common.components.filters.FilterProduct
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
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

class SharedWishlistOwnDetailViewModel(
  private val wishlistId: String,
  wishlistName: String,
  target: String?,
  private val fetchWishlistUseCase: FetchWishlistUseCase,
  private val fetchWishlistItemsUseCase: FetchWishlistItemsUseCase,
  private val unshareWishlistUseCase: UnshareWishlistUseCase,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow(ViewModelState(wishlistId, wishlistName, target))

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

  private val uiSideEffectChannel = Channel<SharedWishlistOwnDetailUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  fun onOpenItemDetail(item: WishlistItem) {
    viewModelState.update { state ->
      state.copy(
        itemSelected = item,
        isItemDetailModalOpen = true
      )
    }
  }

  fun onChangeProductFilters(filters: List<FilterProduct>) {
    viewModelState.update { state -> state.copy(filters = filters) }
  }

  fun onBackToPrivate() {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      runCatching {
        unshareWishlistUseCase(wishlistId).getOrThrow()
      }.onSuccess {
        viewModelState.update { state -> state.copy(isLoading = false) }
        uiSideEffectChannel.send(SharedWishlistOwnDetailUiSideEffect.WishlistUnshared)
      }.onFailure { error ->
        viewModelState.update { state ->
          state.copy(
            isLoading = false,
            error = error,
          )
        }
      }
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
      val wishlistDeferred = async { fetchWishlistUseCase(wishlistId) }
      val itemsDeferred = async { fetchWishlistItemsUseCase(wishlistId) }

      val wishlistResult = wishlistDeferred.await()
      val itemsResult = itemsDeferred.await()

      viewModelState.update { state ->
        state.copy(
          isLoadingFullscreen = false,
          wishlist = wishlistResult.getOrNull() as? Wishlist.Shared,
          items = itemsResult.getOrDefault(emptyList()),
          error = itemsResult.exceptionOrNull()
        )
      }
    }
  }

  private data class ViewModelState(
    val sharedWishlistId: String,
    val wishlistName: String,
    val wishlistTarget: String?,
    val wishlist: Wishlist.Shared? = null,
    val items: List<WishlistItem> = emptyList(),
    val filters: List<FilterProduct> = emptyList(),
    val isItemDetailModalOpen: Boolean = false,
    val itemSelected: WishlistItem? = null,
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
            items = items.sorted().applyFilters(filters),
            itemSelected = itemSelected,
            productFilters = filters,
            isItemDetailModalOpen = isItemDetailModalOpen,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }

    private fun List<WishlistItem>.sorted() = sortedWith(
      compareBy<WishlistItem> { it.purchased != null }
        .thenByDescending { it.priority.weight }
        .thenBy { it.createdAt }
    )

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
  }
}
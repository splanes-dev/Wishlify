package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.ShareWishlistSecretSantaUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistsUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SecretSantaShareWishlistViewModel(
  private val eventId: String,
  private val fetchWishlistsUseCase: FetchWishlistsUseCase,
  private val fetchWishlistItemsUseCase: FetchWishlistItemsUseCase,
  private val shareWishlistSecretSantaUseCase: ShareWishlistSecretSantaUseCase,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchWishlists() }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<SecretSantaShareWishlistUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  fun onShareWishlist(wishlist: Wishlist.Own) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      shareWishlistSecretSantaUseCase(eventId, wishlist.id)
        .onSuccess {
          viewModelState.update { state -> state.copy(isLoading = false) }
          uiSideEffectChannel.send(SecretSantaShareWishlistUiSideEffect.WishlistShared)
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

  fun onSelectWishlist(wishlist: Wishlist.Own?) {
    viewModelState.update { state -> state.copy(wishlistSelected = wishlist) }
  }

  fun onOpenWishlist(wishlist: Wishlist.Own) {
    viewModelState.update { state ->
      state.copy(
        wishlistOpened = wishlist,
        isLoadingFullscreen = true
      )
    }
    viewModelScope.launch {
      val result = fetchWishlistItemsUseCase(wishlist.id)
      viewModelState.update { state ->
        state.copy(
          items = result.getOrDefault(emptyList()),
          isLoadingFullscreen = false,
          error = result.exceptionOrNull()
        )
      }
    }
  }

  fun onOpenItemDetailModal(item: WishlistItem) {
    viewModelState.update { state ->
      state.copy(
        isItemDetailModalOpen = true,
        itemDetailOpened = item
      )
    }
  }

  fun onCloseItemDetailModal() {
    viewModelState.update { state ->
      state.copy(
        isItemDetailModalOpen = false,
        itemDetailOpened = null
      )
    }
  }

  fun onCloseWishlist() {
    viewModelState.update { state ->
      state.copy(
        isItemDetailModalOpen = false,
        itemDetailOpened = null,
        items = emptyList(),
        wishlistOpened = null
      )
    }
  }

  private suspend fun fetchWishlists() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchWishlistsUseCase()
    viewModelState.update { state ->
      state.copy(
        wishlists = result.getOrNull()
          ?.filterIsInstance<Wishlist.Own>()
          ?.filter { wishlist -> wishlist.numOfNonPurchasedItems > 0 }
          .orEmpty(),
        isLoadingFullscreen = false,
        error = result.exceptionOrNull()
      )
    }
  }

  private data class ViewModelState(
    val wishlists: List<Wishlist.Own> = emptyList(),
    val wishlistSelected: Wishlist.Own? = null,
    val wishlistOpened: Wishlist.Own? = null,
    val items: List<WishlistItem> = emptyList(),
    val isItemDetailModalOpen: Boolean = false,
    val itemDetailOpened: WishlistItem? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    fun toUiState(errorUiMapper: ErrorUiMapper) = when {
      isLoadingFullscreen ->
        SecretSantaShareWishlistUiState.Loading(wishlist = wishlistOpened)

      wishlists.isEmpty() || (wishlistOpened != null && items.isEmpty()) ->
        SecretSantaShareWishlistUiState.Empty(wishlist = wishlistOpened)

      wishlistOpened != null ->
        SecretSantaShareWishlistUiState.WishlistDetail(
          wishlist = wishlistOpened,
          items = items,
          isDetailModalOpen = isItemDetailModalOpen,
          itemSelected = itemDetailOpened,
        )

      else ->
        SecretSantaShareWishlistUiState.Wishlists(
          wishlists = wishlists.sortedByDescending { it.createdAt },
          wishlistSelected = wishlistSelected,
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )
    }
  }
}
package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistType
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistsUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WishlistsListViewModel(
  private val fetchWishlistsUseCase: FetchWishlistsUseCase,
  private val deleteWishlistUseCase: DeleteWishlistUseCase,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchWishlists(tab = WishlistsTab.Own) }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  fun onTabClick(tab: WishlistsTab) {
    val currentState = viewModelState.getAndUpdate { state ->
      if (state.tabSelected == tab) {
        state
      } else {
        state.copy(tabSelected = tab)
      }
    }
    if (currentState.tabSelected != tab) {
      fetchWishlists(tab)
    }
  }

  fun onNewWishlistResult(created: Boolean) {
    if (created) {
      fetchWishlists(tab = viewModelState.value.tabSelected)
    }
  }

  fun onUpdateWishlistResult(updated: Boolean) {
    if (updated) {
      fetchWishlists(tab = viewModelState.value.tabSelected)
    }
  }

  fun onWishlistShared(name: String) {
    viewModelState.update { state -> state.copy(sharedWishlistFeedback = name) }
  }

  fun onDeleteWishlist(wishlist: Wishlist) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      deleteWishlistUseCase(wishlist)
        .onSuccess {
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              wishlists = state.wishlists - wishlist
            )
          }
        }
        .onFailure { error ->
          viewModelState.update { state ->
            state.copy(isLoading = false, error = error)
          }
        }
    }
  }

  fun onClearSharedWishlistFeedback() {
    viewModelState.update { state -> state.copy(sharedWishlistFeedback = null) }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private fun fetchWishlists(tab: WishlistsTab) {
    viewModelState.update { state ->
      state.copy(isLoadingFullscreen = true)
    }
    viewModelScope.launch {
      val type = when (tab) {
        WishlistsTab.Own -> WishlistType.Own
        WishlistsTab.ThirdParty -> WishlistType.ThirdParty
      }

      fetchWishlistsUseCase(type = type)
        .onSuccess { wishlists ->
          viewModelState.update { state ->
            state.copy(
              isLoadingFullscreen = false,
              wishlists = wishlists,
            )
          }
        }
        .onFailure { error ->
          viewModelState.update { state ->
            state.copy(
              isLoadingFullscreen = false,
              error = error,
            )
          }
        }
    }
  }

  private data class ViewModelState(
    val tabSelected: WishlistsTab = WishlistsTab.Own,
    val wishlists: List<Wishlist> = emptyList(),
    val sharedWishlistFeedback: String? = null,
    val isLoadingFullscreen: Boolean = false,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {

    fun toUiState(errorUiMapper: ErrorUiMapper) =
      when {
        isLoadingFullscreen ->
          WishlistsListUiState.Loading(tabSelected)

        wishlists.isEmptyState(tabSelected) ->
          WishlistsListUiState.Empty(
            tabSelected = tabSelected,
            sharedWishlistFeedback = sharedWishlistFeedback,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )

        else ->
          WishlistsListUiState.Listing(
            tabSelected = tabSelected,
            wishlistsOwn = wishlists.own().sortedByDescending { it.createdAt },
            wishlistsThirdParty = wishlists.thirdParty().sortedByDescending { it.createdAt },
            sharedWishlistFeedback = sharedWishlistFeedback,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }

    private fun List<Wishlist>.isEmptyState(tab: WishlistsTab) =
      isEmpty() ||
          (tab == WishlistsTab.Own && own().isEmpty()) ||
          (tab == WishlistsTab.ThirdParty && thirdParty().isEmpty())
  }
}

private fun List<Wishlist>.own() = filterIsInstance<Wishlist.Own>()
private fun List<Wishlist>.thirdParty() = filterIsInstance<Wishlist.ThirdParty>()
package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistType
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistsUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.UnshareWishlistUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.model.SharedWishlistsTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SharedWishlistsListViewModel(
  private val fetchSharedWishlistsUseCase: FetchSharedWishlistsUseCase,
  private val unshareWishlistUseCase: UnshareWishlistUseCase,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchSharedWishlists(tab = SharedWishlistsTab.Own) }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  fun onTabClick(tab: SharedWishlistsTab) {
    val currentState = viewModelState.getAndUpdate { state ->
      if (state.tabSelected == tab) {
        state
      } else {
        state.copy(tabSelected = tab)
      }
    }
    if (currentState.tabSelected != tab) {
      fetchSharedWishlists(tab)
    }
  }

  fun onReloadWishlists() {
    val tab = viewModelState.value.tabSelected
    fetchSharedWishlists(tab)
  }

  fun onSharedBackToPrivate(wishlist: SharedWishlist) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      unshareWishlistUseCase(wishlist)
        .onSuccess {
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              wishlists = state.wishlists - wishlist,
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

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private fun fetchSharedWishlists(tab: SharedWishlistsTab) {
    viewModelState.update { state ->
      state.copy(isLoadingFullscreen = true)
    }
    viewModelScope.launch {
      val type = when (tab) {
        SharedWishlistsTab.Own -> SharedWishlistType.Own
        SharedWishlistsTab.ThirdParty -> SharedWishlistType.ThirdParty
      }

      fetchSharedWishlistsUseCase(type = type)
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
    val tabSelected: SharedWishlistsTab = SharedWishlistsTab.Own,
    val wishlists: List<SharedWishlist> = emptyList(),
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {

    fun toUiState(errorUiMapper: ErrorUiMapper): SharedWishlistsListUiState =
      when {
        isLoadingFullscreen ->
          SharedWishlistsListUiState.Loading(tabSelected)

        wishlists.isEmptyState(tabSelected) ->
          SharedWishlistsListUiState.Empty(
            tabSelected = tabSelected,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )

        else ->
          SharedWishlistsListUiState.Listing(
            tabSelected = tabSelected,
            wishlistsOwn = wishlists.sorted().filterIsInstance<SharedWishlist.Own>(),
            wishlistsThirdParty = wishlists.sorted().filterIsInstance<SharedWishlist.ThirdParty>(),
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }

    private fun List<SharedWishlist>.sorted() = sortedWith(
      compareByDescending { it.deadline }
    )

    private fun List<SharedWishlist>.isEmptyState(tab: SharedWishlistsTab) =
      isEmpty() ||
          (tab == SharedWishlistsTab.Own && own().isEmpty()) ||
          (tab == SharedWishlistsTab.ThirdParty && thirdParty().isEmpty())
  }
}


private fun List<SharedWishlist>.own() = filterIsInstance<SharedWishlist.Own>()
private fun List<SharedWishlist>.thirdParty() = filterIsInstance<SharedWishlist.ThirdParty>()
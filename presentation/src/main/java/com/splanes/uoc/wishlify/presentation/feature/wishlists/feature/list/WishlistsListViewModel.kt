package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.UnshareWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.AddWishlistEditorFromLinkUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchCategoriesUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistsUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistNewItemByShare
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WishlistsListViewModel(
  private val fetchWishlistsUseCase: FetchWishlistsUseCase,
  private val deleteWishlistUseCase: DeleteWishlistUseCase,
  private val fetchCategoriesUseCase: FetchCategoriesUseCase,
  private val unshareWishlistUseCase: UnshareWishlistUseCase,
  private val addWishlistEditorFromLinkUseCase: AddWishlistEditorFromLinkUseCase,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())
  private var itemByShare: WishlistNewItemByShare? = null

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchWishlistsAndCategories() }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  fun onAddToEditorsDeeplinkOpened(token: String) {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    viewModelScope.launch {
      addWishlistEditorFromLinkUseCase(token)
        .onSuccess { fetchWishlists() }
        .onFailure { viewModelState.update { state -> state.copy(isLoadingFullscreen = false) } }
    }
  }

  fun onNewItemByUrl(url: String) {
    itemByShare = WishlistNewItemByShare.Url(url)
    viewModelScope.launch { fetchWishlistsAndCategories() }
  }

  fun onNewItemByUri(uri: String) {
    itemByShare = WishlistNewItemByShare.Uri(uri)
    viewModelScope.launch { fetchWishlistsAndCategories() }
  }

  fun onUpdateFilters(filtersState: WishlistsFiltersState) {
    viewModelState.update { state -> state.copy(filtersState = filtersState) }
  }

  fun onNewWishlistResult(created: Boolean) {
    if (created) {
      viewModelScope.launch {
        fetchWishlists()
      }
    }
  }

  fun onUpdateWishlistResult(updated: Boolean) {
    if (updated) {
      viewModelScope.launch {
        fetchWishlists()
      }
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

  fun onSharedBackToPrivate(wishlist: Wishlist) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      unshareWishlistUseCase(wishlist.id)
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

  fun onCloseWishlistSelectionModal() {
    itemByShare = null
    viewModelState.update { state ->
      state.copy(
        isWishlistSelectionModalOpen = false,
        wishlistNewItemByShare = null,
      )
    }
  }

  fun onClearSharedWishlistFeedback() {
    viewModelState.update { state -> state.copy(sharedWishlistFeedback = null) }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private suspend fun fetchWishlists() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    fetchWishlistsUseCase()
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

  private suspend fun fetchWishlistsAndCategories() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val wishlistsResult = fetchWishlistsUseCase()
    val categoriesResult = fetchCategoriesUseCase()
    viewModelState.update { state ->
      state.copy(
        isLoadingFullscreen = false,
        wishlists = wishlistsResult.getOrDefault(emptyList()),
        categories = categoriesResult.getOrDefault(emptyList()),
        isWishlistSelectionModalOpen = itemByShare != null,
        wishlistNewItemByShare = itemByShare,
        error = wishlistsResult.exceptionOrNull()
      )
    }
  }

  private data class ViewModelState(
    val wishlists: List<Wishlist> = emptyList(),
    val filtersState: WishlistsFiltersState = WishlistsFiltersState(),
    val categories: List<Category> = emptyList(),
    val sharedWishlistFeedback: String? = null,
    val isWishlistSelectionModalOpen: Boolean = false,
    val wishlistNewItemByShare: WishlistNewItemByShare? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {

    fun toUiState(errorUiMapper: ErrorUiMapper) =
      when {
        isLoadingFullscreen ->
          WishlistsListUiState.Loading

        wishlists.withFilters(filtersState).isEmpty() ->
          WishlistsListUiState.Empty(
            filtersState = filtersState,
            categories = categories,
            sharedWishlistFeedback = sharedWishlistFeedback,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )

        else ->
          WishlistsListUiState.Listing(
            filtersState = filtersState,
            categories = categories,
            wishlists = wishlists.withFilters(filtersState).sort(),
            sharedWishlistFeedback = sharedWishlistFeedback,
            isWishlistSelectionModalOpen = isWishlistSelectionModalOpen,
            wishlistNewItemByShare = wishlistNewItemByShare,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }
  }
}

private fun List<Wishlist>.withFilters(filters: WishlistsFiltersState) =
  this
    .filter { wishlist ->
      when (filters.target) {
        WishlistsFilter.Own -> wishlist is Wishlist.Own
        WishlistsFilter.ThirdParty -> wishlist is Wishlist.ThirdParty
        else -> true
      }
    }
    .filter { wishlist ->
      when (val filter = filters.category) {
        is WishlistsFilter.Categories -> wishlist.category?.category in filter.values
        else -> true
      }
    }
    .filter { wishlist ->
      when (filters.shareStatus) {
        WishlistsFilter.NotShared -> wishlist !is Wishlist.Shared
        WishlistsFilter.OnSecretSantaEvent -> wishlist is Wishlist.Shared && wishlist.event is Wishlist.SecretSantaEvent
        WishlistsFilter.OnSharedWishlist -> wishlist is Wishlist.Shared && wishlist.event is Wishlist.SharedWishlistEvent
        else -> true
      }
    }
    .filter { wishlist ->
      when (filters.availability) {
        WishlistsFilter.ItemsAvailable -> wishlist.numOfNonPurchasedItems > 0
        WishlistsFilter.ItemsNotAvailable -> wishlist.numOfNonPurchasedItems == 0
        else -> true
      }
    }

private fun List<Wishlist>.sort() = sortedWith(
  compareBy<Wishlist> { it.isFinished() }
    .thenByDescending { it.createdAt }
)
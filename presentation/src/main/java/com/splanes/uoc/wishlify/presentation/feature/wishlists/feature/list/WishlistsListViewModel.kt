package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart {  }
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
        state.copy(
          tabSelected = tab,
          isLoading = true
        )
      }
    }
    if (currentState.tabSelected != tab) {
      viewModelScope.launch {
        // TODO: fetch wishlists & update
      }
    }
  }

  fun onNewWishlistResult(created: Boolean) {

  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private data class ViewModelState(
    val tabSelected: WishlistsTab = WishlistsTab.Own,
    val wishlists: List<Any> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {

    fun toUiState(errorUiMapper: ErrorUiMapper) =
      when {
        wishlists.isEmpty() ->
          WishlistsListUiState.Empty(
            tabSelected = tabSelected,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )

        else ->
          WishlistsListUiState.Listing(
            tabSelected = tabSelected,
            wishlists = wishlists,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }
    }
}
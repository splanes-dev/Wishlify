package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistsUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SharedWishlistsListViewModel(
  private val fetchSharedWishlistsUseCase: FetchSharedWishlistsUseCase,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchSharedWishlists() }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  fun onReloadWishlists() {
    fetchSharedWishlists()
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private fun fetchSharedWishlists() {
    viewModelState.update { state ->
      state.copy(isLoadingFullscreen = true)
    }
    viewModelScope.launch {

      fetchSharedWishlistsUseCase()
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
    val wishlists: List<SharedWishlist> = emptyList(),
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {

    fun toUiState(errorUiMapper: ErrorUiMapper): SharedWishlistsListUiState =
      when {
        isLoadingFullscreen ->
          SharedWishlistsListUiState.Loading

        wishlists.isEmpty() ->
          SharedWishlistsListUiState.Empty

        else ->
          SharedWishlistsListUiState.Listing(
            wishlists = wishlists.sorted(),
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }

    private fun List<SharedWishlist>.sorted() = sortedWith(
      compareByDescending { it.deadline }
    )

  }
}
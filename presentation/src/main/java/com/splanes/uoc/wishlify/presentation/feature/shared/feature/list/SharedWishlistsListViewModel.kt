package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.notifications.usecase.IsPermissionModalVisibleUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.AddSharedWishlistParticipantByTokenUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistsUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Coordinates the shared wishlists list, including invitation-link joins and list refreshes.
 */
class SharedWishlistsListViewModel(
  private val fetchSharedWishlistsUseCase: FetchSharedWishlistsUseCase,
  private val isPermissionModalVisibleUseCase: IsPermissionModalVisibleUseCase,
  private val addSharedWishlistParticipantByTokenUseCase: AddSharedWishlistParticipantByTokenUseCase,
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

  /**
   * Adds the current user to a shared wishlist using an invitation token and refreshes the list.
   */
  fun onJoinToParticipantsByToken(token: String) {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    viewModelScope.launch {
      addSharedWishlistParticipantByTokenUseCase(token)
      onReloadWishlists()
    }
  }

  /**
   * Reloads the shared wishlists list.
   */
  fun onReloadWishlists() {
    fetchSharedWishlists()
  }

  /**
   * Clears the current UI error.
   */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /**
   * Returns the shared wishlist already loaded in memory, waiting briefly until the initial fetch
   * completes.
   */
  suspend fun fetchSharedWishlistById(id: String): SharedWishlist {
    val currentState = viewModelState.value
    if (currentState.wishlists.isNotEmpty()) {
      return currentState.wishlists.first { it.id == id }
    } else {
      delay(250.milliseconds)
      return fetchSharedWishlistById(id)
    }
  }

  /**
   * Loads the current shared wishlists and resolves whether the notification permission modal
   * should be displayed.
   */
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
              isPermissionModalVisible = isPermissionModalVisibleUseCase(),
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
    val isPermissionModalVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {

    /**
     * Maps internal state to the screen representation shown by the list UI.
     */
    fun toUiState(errorUiMapper: ErrorUiMapper): SharedWishlistsListUiState =
      when {
        isLoadingFullscreen ->
          SharedWishlistsListUiState.Loading

        wishlists.isEmpty() ->
          SharedWishlistsListUiState.Empty

        else ->
          SharedWishlistsListUiState.Listing(
            wishlists = wishlists.sorted(),
            isPermissionModalVisible = isPermissionModalVisible,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }

    /**
     * Sorts wishlists by deadline so the closest ones appear first.
     */
    private fun List<SharedWishlist>.sorted() = sortedWith(
      compareByDescending { it.deadline }
    )

  }
}

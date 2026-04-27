package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaWishlist
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.UnshareWishlistSecretSantaUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
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

/**
 * Loads the Secret Santa wishlist shown to the current user and coordinates share removal when
 * the wishlist belongs to the current participant.
 */
class SecretSantaWishlistViewModel(
  private val eventId: String,
  private val wishlistOwnerId: String?,
  private val isOwnWishlist: Boolean,
  private val fetchSecretSantaWishlistUseCase: FetchSecretSantaWishlistUseCase,
  private val fetchSecretSantaWishlistItemsUseCase: FetchSecretSantaWishlistItemsUseCase,
  private val unshareWishlistSecretSantaUseCase: UnshareWishlistSecretSantaUseCase,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState(isOwnWishlist))
  val uiState = viewModelState.asStateFlow()
    .onStart { fetchWishlistAndItems() }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<SecretSantaWishlistUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  /**
   * Reloads the wishlist after a nested flow has modified its sharing state.
   */
  fun onWishlistChanged() {
    viewModelScope.launch { fetchWishlistAndItems() }
  }

  /**
   * Removes the current user's wishlist share from the Secret Santa event.
   */
  fun onDeleteWishlist() {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      unshareWishlistSecretSantaUseCase(eventId)
        .onSuccess {
          viewModelState.update { state -> state.copy(isLoading = false) }
          uiSideEffectChannel.send(SecretSantaWishlistUiSideEffect.WishlistRemoved)
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

  /**
   * Opens the detail modal for a specific wishlist item.
   */
  fun onOpenItemDetailModal(item: WishlistItem) {
    viewModelState.update { state -> state.copy(itemSelected = item) }
  }

  /**
   * Closes the currently open wishlist item detail modal.
   */
  fun onCloseItemDetailModal() {
    viewModelState.update { state -> state.copy(itemSelected = null) }
  }

  /**
   * Loads the Secret Santa wishlist header and its items in parallel.
   */
  private suspend fun fetchWishlistAndItems() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    coroutineScope {
      val wishlistDeferred = async { fetchSecretSantaWishlistUseCase(eventId, wishlistOwnerId, isOwnWishlist) }
      val itemsDeferred = async { fetchSecretSantaWishlistItemsUseCase(eventId, wishlistOwnerId, isOwnWishlist) }

      val wishlistResult = wishlistDeferred.await()
      val itemsResult = itemsDeferred.await()

      viewModelState.update { state ->
        state.copy(
          isLoadingFullscreen = false,
          wishlist = wishlistResult.getOrNull(),
          items = itemsResult.getOrDefault(emptyList())
        )
      }
    }
  }

  private data class ViewModelState(
    val isOwnWishlist: Boolean,
    val wishlist: SecretSantaWishlist? = null,
    val items: List<WishlistItem> = emptyList(),
    val itemSelected: WishlistItem? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    /**
     * Maps internal state to the wishlist detail UI contract.
     */
    fun toUiState(errorUiMapper: ErrorUiMapper) = when {
      isLoadingFullscreen ->
        SecretSantaWishlistUiState.Loading(wishlistName = wishlist?.title)

      wishlist == null || items.isEmpty() ->
        SecretSantaWishlistUiState.Error(wishlistName = wishlist?.title)

      else ->
        SecretSantaWishlistUiState.Listing(
          wishlist = wishlist,
          isOwnWishlist = isOwnWishlist,
          items = items,
          itemSelected = itemSelected,
          isItemDetailOpened = itemSelected != null,
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )
    }
  }
}

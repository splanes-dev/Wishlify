package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WishlistDetailRoute(
  viewModel: WishlistDetailViewModel,
  onNavToEditWishlist: (wishlistId: String) -> Unit,
  onNavToNewItem: (link: String?) -> Unit,
  onNavToEditItem: (itemId: String) -> Unit,
  onNavToShare: () -> Unit,
  onBack: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        is WishlistDetailUiSideEffect.NavToEdit -> {
          onNavToEditItem(effect.itemId)
        }

        WishlistDetailUiSideEffect.WishlistDeleted -> onBack()
      }
    }
  }

  when (val state = uiState) {
    is WishlistDetailUiState.Empty ->
      WishlistDetailEmptyScreen(
        uiState = state,
        onEditWishlist = { w -> onNavToEditWishlist(w.id) },
        onDeleteWishlist = viewModel::onDeleteWishlist,
        onCreateItem = onNavToNewItem,
        onBack = onBack,
        onChangeItemByLinkModalVisibility = viewModel::onChangeItemByLinkModalVisibility,
        onClearInputError = viewModel::onClearInputError,
        onDismissError = viewModel::onDismissError,
      )

    is WishlistDetailUiState.Error ->
      WishlistDetailErrorScreen(
        uiState = state,
        onBack = onBack
      )

    is WishlistDetailUiState.Listing ->
      WishlistDetailScreen(
        uiState = state,
        onEditWishlist = { w -> onNavToEditWishlist(w.id) },
        onDeleteWishlist = viewModel::onDeleteWishlist,
        onCreateItem = onNavToNewItem,
        onShare = onNavToShare,
        onBack = onBack,
        onChangeItemByLinkModalVisibility = viewModel::onChangeItemByLinkModalVisibility,
        onCloseItemDetailModal = viewModel::onCloseItemDetailModal,
        onItemAction = viewModel::onItemAction,
        onClearInputError = viewModel::onClearInputError,
        onDismissError = viewModel::onDismissError,
      )

    is WishlistDetailUiState.Loading ->
      WishlistDetailLoadingScreen(
        uiState = state,
        onBack = onBack,
      )
  }
}
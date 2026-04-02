package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WishlistsListRoute(
  viewModel: WishlistsListViewModel,
  onNavToCreateWishlist: (isOwn: Boolean) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is WishlistsListUiState.Empty ->
      WishlistsListEmptyScreen(
        uiState = state,
        onTabClick = viewModel::onTabClick,
        onCreateWishlist = { onNavToCreateWishlist(true) },
        onDismissError = viewModel::onDismissError
      )

    is WishlistsListUiState.Listing ->
      WishlistsListScreen(
        uiState = state,
      )
  }
}
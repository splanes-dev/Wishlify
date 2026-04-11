package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist

@Composable
fun WishlistsListRoute(
  viewModel: WishlistsListViewModel,
  onNavToNewWishlist: (isOwn: Boolean) -> Unit,
  onNavToEditWishlist: (wishlist: Wishlist) -> Unit,
  onNavToShareWishlist: (wishlist: Wishlist) -> Unit,
  onNavToWishlistDetail: (wishlist: Wishlist) -> Unit,
  onNavToAdminCategories: () -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is WishlistsListUiState.Loading ->
      WishlistsListLoadingScreen(
        uiState = state,
        onTabClick = viewModel::onTabClick,
      )

    is WishlistsListUiState.Empty ->
      WishlistsListEmptyScreen(
        uiState = state,
        onTabClick = viewModel::onTabClick,
        onCreateWishlist = { onNavToNewWishlist(it) },
        onAdminCategories = onNavToAdminCategories,
        onClearSharedWishlistFeedback = viewModel::onClearSharedWishlistFeedback,
        onDismissError = viewModel::onDismissError
      )

    is WishlistsListUiState.Listing ->
      WishlistsListScreen(
        uiState = state,
        onTabClick = viewModel::onTabClick,
        onCreateWishlist = { onNavToNewWishlist(it) },
        onWishlistClick = onNavToWishlistDetail,
        onEditWishlist = onNavToEditWishlist,
        onShareWishlist = onNavToShareWishlist,
        onDeleteWishlist = viewModel::onDeleteWishlist,
        onClearSharedWishlistFeedback = viewModel::onClearSharedWishlistFeedback,
        onAdminCategories = onNavToAdminCategories,
        onDismissError = viewModel::onDismissError
      )
  }
}
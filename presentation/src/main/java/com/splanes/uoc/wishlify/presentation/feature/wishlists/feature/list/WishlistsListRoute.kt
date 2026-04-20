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
  onNavToWishlistSharedDetail: (wishlist: Wishlist) -> Unit,
  onNavToAdminCategories: () -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is WishlistsListUiState.Loading ->
      WishlistsListLoadingScreen()

    is WishlistsListUiState.Empty ->
      WishlistsListEmptyScreen(
        uiState = state,
        onCreateWishlist = { onNavToNewWishlist(it) },
        onUpdateFilters = viewModel::onUpdateFilters,
        onAdminCategories = onNavToAdminCategories,
        onClearSharedWishlistFeedback = viewModel::onClearSharedWishlistFeedback,
        onDismissError = viewModel::onDismissError
      )

    is WishlistsListUiState.Listing ->
      WishlistsListScreen(
        uiState = state,
        onUpdateFilters = viewModel::onUpdateFilters,
        onCreateWishlist = { onNavToNewWishlist(it) },
        onWishlistClick = { wishlist ->
          when (wishlist) {
            is Wishlist.Own,
            is Wishlist.ThirdParty -> onNavToWishlistDetail(wishlist)
            is Wishlist.Shared -> onNavToWishlistSharedDetail(wishlist)
          }
        },
        onEditWishlist = onNavToEditWishlist,
        onShareWishlist = onNavToShareWishlist,
        onDeleteWishlist = viewModel::onDeleteWishlist,
        onClearSharedWishlistFeedback = viewModel::onClearSharedWishlistFeedback,
        onAdminCategories = onNavToAdminCategories,
        onSharedBackToPrivate = viewModel::onSharedBackToPrivate,
        onDismissError = viewModel::onDismissError
      )
  }
}
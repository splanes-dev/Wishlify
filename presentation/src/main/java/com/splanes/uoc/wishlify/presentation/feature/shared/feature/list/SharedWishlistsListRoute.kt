package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist

@Composable
fun SharedWishlistsListRoute(
  viewModel: SharedWishlistsListViewModel,
  onNavToOwnSharedWishlistDetail: (wishlist: SharedWishlist) -> Unit,
  onNavToThirdPartySharedWishlistDetail: (wishlist: SharedWishlist) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is SharedWishlistsListUiState.Empty ->
      SharedWishlistsListEmptyScreen(
        uiState = state,
        onTabClick = viewModel::onTabClick
      )

    is SharedWishlistsListUiState.Listing ->
      SharedWishlistsListScreen(
        uiState = state,
        onWishlistClick = { wishlist ->
          when (wishlist) {
            is SharedWishlist.Own ->
              onNavToOwnSharedWishlistDetail(wishlist)

            is SharedWishlist.ThirdParty ->
              onNavToThirdPartySharedWishlistDetail(wishlist)
          }
        },
        onTabClick = viewModel::onTabClick,
        onSharedBackToPrivate = viewModel::onSharedBackToPrivate
      )

    is SharedWishlistsListUiState.Loading ->
      SharedWishlistsListLoadingScreen(
        uiState = state,
        onTabClick = viewModel::onTabClick
      )
  }
}
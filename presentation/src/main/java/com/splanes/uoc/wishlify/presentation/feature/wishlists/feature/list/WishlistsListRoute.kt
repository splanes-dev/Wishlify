package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistNewItemByShare
import com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation.WishlistExternalAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation.WishlistExternalActionHandler

@Composable
fun WishlistsListRoute(
  viewModel: WishlistsListViewModel,
  externalActionHandler: WishlistExternalActionHandler,
  onNavToNewWishlist: (isOwn: Boolean) -> Unit,
  onNavToEditWishlist: (wishlist: Wishlist) -> Unit,
  onNavToShareWishlist: (wishlist: Wishlist) -> Unit,
  onNavToWishlistDetail: (wishlist: Wishlist) -> Unit,
  onNavToWishlistSharedDetail: (wishlist: Wishlist) -> Unit,
  onNavToAdminCategories: () -> Unit,
  onNavToNewItem: (wishlist: Wishlist, WishlistNewItemByShare) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(externalActionHandler) {
    externalActionHandler.consume { action ->
      when (action) {
        is WishlistExternalAction.JoinToEditorByToken ->
          viewModel.onAddToEditorsDeeplinkOpened(action.token)

        is WishlistExternalAction.NewItemByUri ->
          viewModel.onNewItemByUri(action.uri)

        is WishlistExternalAction.NewItemByUrl ->
          viewModel.onNewItemByUrl(action.url)
      }
    }
  }

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
        onWishlistClickWithCreate = { wishlist, itemByShare ->
          when (wishlist) {
            is Wishlist.Own,
            is Wishlist.ThirdParty -> onNavToNewItem(wishlist, itemByShare)
            is Wishlist.Shared -> {
              // Do nothing, should not be possible
            }
          }
        },
        onEditWishlist = onNavToEditWishlist,
        onShareWishlist = onNavToShareWishlist,
        onDeleteWishlist = viewModel::onDeleteWishlist,
        onClearSharedWishlistFeedback = viewModel::onClearSharedWishlistFeedback,
        onAdminCategories = onNavToAdminCategories,
        onSharedBackToPrivate = viewModel::onSharedBackToPrivate,
        onCloseWishlistSelectionModal = viewModel::onCloseWishlistSelectionModal,
        onDismissError = viewModel::onDismissError
      )
  }
}
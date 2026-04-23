package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlistExternalAction
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlistExternalActionHandler

@Composable
fun SharedWishlistsListRoute(
  viewModel: SharedWishlistsListViewModel,
  externalActionHandler: SharedWishlistExternalActionHandler,
  onNavToThirdPartySharedWishlistDetail: (wishlist: SharedWishlist) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(externalActionHandler) {
    externalActionHandler.consume { action ->
      when (action) {
        is SharedWishlistExternalAction.JoinToParticipantsByToken -> {
          viewModel.onJoinToParticipantsByToken(action.token)
          externalActionHandler.clean()
        }

        is SharedWishlistExternalAction.OpenChatById -> {
          val wishlist = viewModel.fetchSharedWishlistById(action.id)
          onNavToThirdPartySharedWishlistDetail(wishlist)
        }

        is SharedWishlistExternalAction.OpenDetailById -> {
          val wishlist = viewModel.fetchSharedWishlistById(action.id)
          onNavToThirdPartySharedWishlistDetail(wishlist)
          externalActionHandler.clean()
        }
      }
    }
  }

  when (val state = uiState) {
    is SharedWishlistsListUiState.Empty ->
      SharedWishlistsListEmptyScreen()

    is SharedWishlistsListUiState.Listing ->
      SharedWishlistsListScreen(
        uiState = state,
        onWishlistClick = onNavToThirdPartySharedWishlistDetail
      )

    is SharedWishlistsListUiState.Loading ->
      SharedWishlistsListLoadingScreen()
  }
}
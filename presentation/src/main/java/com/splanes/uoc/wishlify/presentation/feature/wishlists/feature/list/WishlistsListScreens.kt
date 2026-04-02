package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.TabSelector
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.FABMenu
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.FABMenuItem
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsTab

@Composable
fun WishlistsListScreen(
  uiState: WishlistsListUiState.Listing
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {

  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistsListEmptyScreen(
  uiState: WishlistsListUiState.Empty,
  onTabClick: (tab: WishlistsTab) -> Unit,
  onCreateWishlist: () -> Unit,
  onDismissError: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.wishlists)) },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = {}
            ) {
              Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = stringResource(R.string.settings)
              )
            }
          }
        )
      },
      floatingActionButton = {
        FABMenu { collapse ->
          FABMenuItem(
            icon = rememberVectorPainter(Icons.Outlined.Sell),
            text = stringResource(R.string.wishlists_category),
            onClick = {
              collapse()
            }
          )
          FABMenuItem(
            icon = painterResource(R.drawable.ic_wishlists),
            text = stringResource(R.string.wishlists_wishlist),
            onClick = {
              onCreateWishlist()
              collapse()
            }
          )
        }
      },
      floatingActionButtonPosition = FabPosition.End
    ) { paddings ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(paddings)
          .padding(
            horizontal = 16.dp,
            vertical = 24.dp
          ),
      ) {

        TabSelector(
          modifier = Modifier.align(Alignment.CenterHorizontally),
          selected = uiState.tabSelected,
          tabs = WishlistsTab.entries.toList(),
          tabText = { tab -> tab.text() },
          onClick = onTabClick
        )

        Spacer(modifier = Modifier.height(80.dp))

        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.wishlists_list_empty_state),
          title = stringResource(R.string.wishlists_list_empty_state_title),
          description = when (uiState.tabSelected) {
            WishlistsTab.Own -> R.string.wishlists_list_own_empty_state_description
            WishlistsTab.ThirdParty -> R.string.wishlists_list_others_empty_state_description
          }.let { id -> stringResource(id) }
        )

      }
    }

    uiState.error?.let { error ->
      ErrorDialog(
        uiModel = error,
        onDismiss = onDismissError,
      )
    }

    if (uiState.isLoading) {
      Loader(modifier = Modifier.fillMaxSize())
    }
  }
}

@Composable
private fun WishlistsTab.text(): String =
  when (this) {
    WishlistsTab.Own -> R.string.wishlists_list_tab_own
    WishlistsTab.ThirdParty -> R.string.wishlists_list_tab_others
  }.let { id -> stringResource(id) }
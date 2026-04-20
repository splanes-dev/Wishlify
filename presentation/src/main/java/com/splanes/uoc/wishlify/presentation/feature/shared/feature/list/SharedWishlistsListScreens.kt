package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components.SharedWishlistCard
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components.SharedWishlistsFinishedHeader
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components.SharedWishlistsSearchBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistsListScreen(
  uiState: SharedWishlistsListUiState.Listing,
  onWishlistClick: (wishlist: SharedWishlist) -> Unit,
) {

  val coroutineScope = rememberCoroutineScope()

  val wishlists = uiState.wishlists

  val existsWishlistsFinished by remember(wishlists) {
    derivedStateOf { wishlists.any { w -> w.isFinished() } }
  }
  var areWishlistsFinishedVisible by remember { mutableStateOf(true) }

  var isSearchModalOpen by remember { mutableStateOf(false) }
  val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.shared_wishlists)) },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = { isSearchModalOpen = true }
            ) {
              Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = stringResource(R.string.search)
              )
            }
          }
        )
      },
    ) { paddings ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddings),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {

        items(
          items = wishlists.filter { !it.isFinished() },
          key = { item -> item.id }
        ) { wishlist ->
          SharedWishlistCard(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .animateItem(),
            sharedWishlist = wishlist,
            onClick = { onWishlistClick(wishlist) }
          )
        }

        if (existsWishlistsFinished) {
          item(key = 1, contentType = "header") {
            SharedWishlistsFinishedHeader(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
              isVisible = areWishlistsFinishedVisible,
              description = htmlString(R.string.shared_wishlists_finished_header_third_party_description),
              onChangeVisibility = { areWishlistsFinishedVisible = !areWishlistsFinishedVisible }
            )
          }

          if (areWishlistsFinishedVisible) {
            items(
              items = wishlists.filter { it.isFinished() },
              key = { item -> item.id }
            ) { wishlist ->
              SharedWishlistCard(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp)
                  .animateItem(),
                sharedWishlist = wishlist,
                onClick = { onWishlistClick(wishlist) }
              )
            }
          }
        }
      }
    }

    SharedWishlistsSearchBottomSheet(
      visible = isSearchModalOpen,
      sheetState = searchSheetState,
      wishlists = wishlists,
      onDismiss = { isSearchModalOpen = false },
      onClick = { wishlist ->
        onWishlistClick(wishlist)
        coroutineScope
          .launch { searchSheetState.hide() }
          .invokeOnCompletion { isSearchModalOpen = false }
      },
    )

    if (uiState.isLoading) {
      Loader(modifier = Modifier.fillMaxSize())
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistsListEmptyScreen() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.shared_wishlists)) },
        )
      }
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
        verticalArrangement = Arrangement.Center,
      ) {

        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.wishlists_list_empty_state),
          title = stringResource(R.string.wishlists_list_empty_state_title),
          description = stringResource(R.string.shared_wishlists_list_others_empty_state_description)
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedWishlistsListLoadingScreen() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.shared_wishlists)) },
        )
      }
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
        verticalArrangement = Arrangement.Center
      ) {

        Loader(
          modifier = Modifier.weight(1f),
          containerColor = Color.Transparent
        )
      }
    }
  }
}
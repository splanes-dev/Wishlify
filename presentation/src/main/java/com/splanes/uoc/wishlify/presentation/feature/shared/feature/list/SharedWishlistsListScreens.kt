package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tune
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.TabSelector
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components.SharedWishlistCard
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components.SharedWishlistMoveToPrivateDialog
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components.SharedWishlistSettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components.SharedWishlistsFinishedHeader
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.model.SharedWishlistsSettings
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.model.SharedWishlistsTab
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistsListScreen(
  uiState: SharedWishlistsListUiState.Listing,
  onTabClick: (tab: SharedWishlistsTab) -> Unit,
  onWishlistClick: (wishlist: SharedWishlist) -> Unit,
  onSharedBackToPrivate: (wishlist: SharedWishlist) -> Unit,
) {

  val coroutineScope = rememberCoroutineScope()

  val wishlists = when (uiState.tabSelected) {
    SharedWishlistsTab.Own -> uiState.wishlistsOwn
    SharedWishlistsTab.ThirdParty -> uiState.wishlistsThirdParty
  }

  val existsWishlistsFinished by remember(wishlists) {
    derivedStateOf { wishlists.any { w -> w.isFinished() } }
  }
  var areWishlistsFinishedVisible by remember { mutableStateOf(true) }

  var itemSelected: SharedWishlist.Own? by remember { mutableStateOf(null) }
  var isSettingsModalOpen by remember { mutableStateOf(false) }
  val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isBackToPrivateDialogOpen by remember { mutableStateOf(false) }

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
              onClick = { /* todo */ }
            ) {
              Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = stringResource(R.string.settings)
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
        stickyHeader(key = 0, contentType = "header") {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                color = WishlifyTheme.colorScheme.surface,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
              )
              .padding(bottom = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            TabSelector(
              modifier = Modifier.fillMaxWidth(),
              selected = uiState.tabSelected,
              tabs = SharedWishlistsTab.entries.toList(),
              tabText = { tab -> tab.text() },
              onClick = onTabClick
            )
          }
        }

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
            onSettingsClick = {
              // Non-expired wishlists has no settings
              Timber.e("Trying to open setting of non-expired wishlist. Should not be possible, no settings available.")
            },
            onClick = { onWishlistClick(wishlist) }
          )
        }

        if (existsWishlistsFinished) {
          item(key = 1, contentType = "header") {
            SharedWishlistsFinishedHeader(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
              isVisible = areWishlistsFinishedVisible,
              description = when (uiState.tabSelected) {
                SharedWishlistsTab.Own -> R.string.shared_wishlists_finished_header_own_description
                SharedWishlistsTab.ThirdParty -> R.string.shared_wishlists_finished_header_third_party_description
              }.let { id -> htmlString(id) },
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
                onSettingsClick = {
                  if (wishlist is SharedWishlist.Own) {
                    itemSelected = wishlist
                    isSettingsModalOpen = true
                  } else {
                    // Third party expired wishlists has no settings
                    Timber.e("Trying to open setting of third party expired wishlist. Should not be possible, no settings available.")
                  }
                },
                onClick = { onWishlistClick(wishlist) }
              )
            }
          }
        }
      }
    }

    itemSelected?.let { wishlist ->

      SharedWishlistSettingsBottomSheet(
        visible = isSettingsModalOpen,
        sheetState = settingsSheetState,
        settings = SharedWishlistsSettings.entries,
        onDismiss = {
          isSettingsModalOpen = false
          itemSelected = null
        },
        onSettingClick = { setting ->
          when (setting) {
            SharedWishlistsSettings.BackToPrivate -> isBackToPrivateDialogOpen = true
          }
          coroutineScope
            .launch { settingsSheetState.hide() }
            .invokeOnCompletion { isSettingsModalOpen = false }
        },
      )

      if (isBackToPrivateDialogOpen) {
        SharedWishlistMoveToPrivateDialog(
          onDismiss = {
            isBackToPrivateDialogOpen = false
            itemSelected = null
          },
          onConfirm = { onSharedBackToPrivate(wishlist) }
        )
      }
    }

    if (uiState.isLoading) {
      Loader(modifier = Modifier.fillMaxSize())
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistsListEmptyScreen(
  uiState: SharedWishlistsListUiState.Empty,
  onTabClick: (tab: SharedWishlistsTab) -> Unit
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
          title = { Text(text = stringResource(R.string.shared_wishlists)) },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = { }
            ) {
              Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = stringResource(R.string.settings)
              )
            }
          }
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
      ) {

        TabSelector(
          modifier = Modifier.fillMaxWidth(),
          selected = uiState.tabSelected,
          tabs = SharedWishlistsTab.entries.toList(),
          tabText = { tab -> tab.text() },
          onClick = onTabClick
        )

        Spacer(modifier = Modifier.height(80.dp))

        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.wishlists_list_empty_state),
          title = stringResource(R.string.wishlists_list_empty_state_title),
          description = when (uiState.tabSelected) {
            SharedWishlistsTab.Own -> R.string.shared_wishlists_list_own_empty_state_description
            SharedWishlistsTab.ThirdParty -> R.string.shared_wishlists_list_others_empty_state_description
          }.let { id -> stringResource(id) }
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedWishlistsListLoadingScreen(
  uiState: SharedWishlistsListUiState.Loading,
  onTabClick: (tab: SharedWishlistsTab) -> Unit
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

        TabSelector(
          modifier = Modifier.fillMaxWidth(),
          selected = uiState.tabSelected,
          tabs = SharedWishlistsTab.entries.toList(),
          tabText = { tab -> tab.text() },
          onClick = onTabClick
        )

        Loader(
          modifier = Modifier.weight(1f),
          containerColor = Color.Transparent
        )
      }
    }
  }
}

@Composable
private fun SharedWishlistsTab.text(): String =
  when (this) {
    SharedWishlistsTab.Own -> R.string.shared_wishlists_list_tab_own
    SharedWishlistsTab.ThirdParty -> R.string.shared_wishlists_list_tab_third_party
  }.let { id -> stringResource(id) }
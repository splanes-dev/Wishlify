package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

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
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ConfirmationDialog
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.TabSelector
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.FABMenu
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.FABMenuItem
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistCard
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistCardSettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistsSearchBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistsSettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistCardSettings
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsSettings
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsTab
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistsListScreen(
  uiState: WishlistsListUiState.Listing,
  onTabClick: (tab: WishlistsTab) -> Unit,
  onCreateWishlist: (isOwn: Boolean) -> Unit,
  onWishlistClick: (Wishlist) -> Unit,
  onEditWishlist: (Wishlist) -> Unit,
  onShareWishlist: (Wishlist) -> Unit,
  onDeleteWishlist: (Wishlist) -> Unit,
  onAdminCategories: () -> Unit,
  onClearSharedWishlistFeedback: () -> Unit,
  onDismissError: () -> Unit,
) {

  val coroutineScope = rememberCoroutineScope()

  val wishlists = when (uiState.tabSelected) {
    WishlistsTab.Own -> uiState.wishlistsOwn
    WishlistsTab.ThirdParty -> uiState.wishlistsThirdParty
  }

  var isSettingsModalOpen by remember { mutableStateOf(false) }
  val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isSearchModalOpen by remember { mutableStateOf(false) }
  val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isWishlistSettingsModalOpen by remember { mutableStateOf(false) }
  val wishlistSettingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isDeleteItemDialogVisible by remember { mutableStateOf(false) }

  var isShareInfoDialogVisible by remember { mutableStateOf(false) }

  var wishlistSelected: Wishlist? by remember { mutableStateOf(null) }

  val resources = LocalResources.current
  val snackbarState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.sharedWishlistFeedback) {
    if (uiState.sharedWishlistFeedback != null) {
      snackbarState.showSnackbar(
        message = resources.getString(
          R.string.wishlists_shared_wishlist_feedback,
          uiState.sharedWishlistFeedback
        ),
        duration = SnackbarDuration.Long,
        withDismissAction = true
      )
      onClearSharedWishlistFeedback()
    }
  }

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
              onClick = { isSettingsModalOpen = true }
            ) {
              Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = stringResource(R.string.settings)
              )
            }
          }
        )
      },
      snackbarHost = {
        SnackbarHost(snackbarState) { data ->
          Snackbar(
            snackbarData = data,
            shape = WishlifyTheme.shapes.small,
            containerColor = WishlifyTheme.colorScheme.successContainer,
            contentColor = WishlifyTheme.colorScheme.onSuccessContainer,
            dismissActionContentColor = WishlifyTheme.colorScheme.onSuccessContainer,
          )
        }
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
              onCreateWishlist(uiState.tabSelected == WishlistsTab.Own)
              collapse()
            }
          )
        }
      },
      floatingActionButtonPosition = FabPosition.End
    ) { paddings ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddings),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        stickyHeader {
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
              tabs = WishlistsTab.entries.toList(),
              tabText = { tab -> tab.text() },
              onClick = onTabClick
            )
          }
        }

        items(
          items = wishlists,
          key = { item -> item.id }
        ) { wishlist ->
          WishlistCard(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .animateItem(),
            wishlist = wishlist,
            onSettingsClick = {
              wishlistSelected = wishlist
              isWishlistSettingsModalOpen = true
            },
            onClick = { onWishlistClick(wishlist) }
          )
        }
      }
    }

    WishlistsSettingsBottomSheet(
      visible = isSettingsModalOpen,
      sheetState = settingsSheetState,
      onDismiss = { isSettingsModalOpen = false },
      onSettingClick = { setting ->
        when (setting) {
          WishlistsSettings.Search -> {
            isSearchModalOpen = true
          }

          WishlistsSettings.Filter -> {
            // TODO
          }

          WishlistsSettings.AdminCategories -> onAdminCategories()
        }
      }
    )

    wishlistSelected?.let { wishlist ->
      WishlistCardSettingsBottomSheet(
        visible = isWishlistSettingsModalOpen,
        sheetState = wishlistSettingsSheetState,
        settings = buildList {
          addAll(WishlistCardSettings.entries)
          if (!wishlist.isShareable()) remove(WishlistCardSettings.Share)
        },
        onDismiss = {
          coroutineScope
            .launch { wishlistSettingsSheetState.hide() }
            .invokeOnCompletion {
              wishlistSelected = null
              isWishlistSettingsModalOpen = false
            }
        },
        onSettingClick = { setting ->
          when (setting) {
            WishlistCardSettings.Edit -> {
              onEditWishlist(wishlist)
              coroutineScope
                .launch { wishlistSettingsSheetState.hide() }
                .invokeOnCompletion {
                  wishlistSelected = null
                  isWishlistSettingsModalOpen = false
                }
            }

            WishlistCardSettings.Share -> {
              isShareInfoDialogVisible = true
              coroutineScope
                .launch { wishlistSettingsSheetState.hide() }
                .invokeOnCompletion { isWishlistSettingsModalOpen = false }
            }

            WishlistCardSettings.Delete -> {
              isDeleteItemDialogVisible = true
              coroutineScope
                .launch { wishlistSettingsSheetState.hide() }
                .invokeOnCompletion { isWishlistSettingsModalOpen = false }
            }
          }
        }
      )
    }

    WishlistsSearchBottomSheet(
      visible = isSearchModalOpen,
      sheetState = searchSheetState,
      wishlists = wishlists,
      onDismiss = { isSearchModalOpen = false },
      onWishlistClick = { wishlist ->
        isSettingsModalOpen = false
        onWishlistClick(wishlist)
      }
    )

    if (isShareInfoDialogVisible) {
      ConfirmationDialog(
        icon = Icons.Rounded.QuestionMark,
        title = stringResource(R.string.error_dialog_title_warning),
        description = stringResource(R.string.wishlists_share_wishlist_with_purchased_items_dialog),
        onDismiss = {
          isShareInfoDialogVisible = false
          wishlistSelected = null
        },
        onConfirm = { wishlistSelected?.let { onShareWishlist(it) } }
      )
    }

    if (isDeleteItemDialogVisible) {
      ConfirmationDialog(
        onDismiss = {
          isDeleteItemDialogVisible = false
          wishlistSelected = null
        },
        onConfirm = { wishlistSelected?.let { onDeleteWishlist(it) } }
      )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistsListEmptyScreen(
  uiState: WishlistsListUiState.Empty,
  onTabClick: (tab: WishlistsTab) -> Unit,
  onCreateWishlist: (isOwn: Boolean) -> Unit,
  onClearSharedWishlistFeedback: () -> Unit,
  onAdminCategories: () -> Unit,
  onDismissError: () -> Unit,
) {

  var isSettingsModalOpen by remember { mutableStateOf(false) }
  val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val resources = LocalResources.current
  val snackbarState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.sharedWishlistFeedback) {
    if (uiState.sharedWishlistFeedback != null) {
      snackbarState.showSnackbar(
        message = resources.getString(
          R.string.wishlists_shared_wishlist_feedback,
          uiState.sharedWishlistFeedback
        ),
        duration = SnackbarDuration.Long,
        withDismissAction = true
      )
      onClearSharedWishlistFeedback()
    }
  }


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
              onClick = { isSettingsModalOpen = true }
            ) {
              Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = stringResource(R.string.settings)
              )
            }
          }
        )
      },
      snackbarHost = {
        SnackbarHost(snackbarState) { data ->
          Snackbar(
            snackbarData = data,
            shape = WishlifyTheme.shapes.small,
            containerColor = WishlifyTheme.colorScheme.successContainer,
            contentColor = WishlifyTheme.colorScheme.onSuccessContainer,
            dismissActionContentColor = WishlifyTheme.colorScheme.onSuccessContainer,
          )
        }
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
              onCreateWishlist(uiState.tabSelected == WishlistsTab.Own)
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
          modifier = Modifier.fillMaxWidth(),
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

    WishlistsSettingsBottomSheet(
      visible = isSettingsModalOpen,
      sheetState = settingsSheetState,
      settings = listOf(WishlistsSettings.AdminCategories),
      onDismiss = { isSettingsModalOpen = false },
      onSettingClick = { onAdminCategories() }
    )

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistsListLoadingScreen(
  uiState: WishlistsListUiState.Loading,
  onTabClick: (tab: WishlistsTab) -> Unit,
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
          tabs = WishlistsTab.entries.toList(),
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
private fun WishlistsTab.text(): String =
  when (this) {
    WishlistsTab.Own -> R.string.wishlists_list_tab_own
    WishlistsTab.ThirdParty -> R.string.wishlists_list_tab_others
  }.let { id -> stringResource(id) }
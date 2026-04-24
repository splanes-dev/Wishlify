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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ConfirmationDialog
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components.SharedWishlistMoveToPrivateDialog
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components.SharedWishlistsFinishedHeader
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.FABMenu
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.FABMenuItem
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.WishlistInfoBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.NewCategoryBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistCard
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistCardSettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistFilterBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistSelectionBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistsFiltersBar
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistsSearchBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistsSettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistCardSettings
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistNewItemByShare
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsFilter
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsSettings
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistsListScreen(
  uiState: WishlistsListUiState.Listing,
  onUpdateFilters: (state: WishlistsFiltersState) -> Unit,
  onCreateWishlist: (isOwn: Boolean) -> Unit,
  onCreateCategory: (name: String, color: Category.CategoryColor) -> Unit,
  onWishlistClick: (Wishlist) -> Unit,
  onWishlistClickWithCreate: (Wishlist, WishlistNewItemByShare) -> Unit,
  onEditWishlist: (Wishlist) -> Unit,
  onShareWishlist: (Wishlist) -> Unit,
  onDeleteWishlist: (Wishlist) -> Unit,
  onAdminCategories: () -> Unit,
  onSharedBackToPrivate: (wishlist: Wishlist) -> Unit,
  onClearSharedWishlistFeedback: () -> Unit,
  onClearNewCategoryNameError: () -> Unit,
  onCloseWishlistSelectionModal: () -> Unit,
  onDismissError: () -> Unit,
) {

  val coroutineScope = rememberCoroutineScope()

  val wishlists = uiState.wishlists

  var isSettingsModalOpen by remember { mutableStateOf(false) }
  val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isSearchModalOpen by remember { mutableStateOf(false) }
  val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isWishlistSettingsModalOpen by remember { mutableStateOf(false) }
  val wishlistSettingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val wishlistSelectionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isDeleteItemDialogVisible by remember { mutableStateOf(false) }

  var isShareInfoDialogVisible by remember { mutableStateOf(false) }

  var wishlistSelected: Wishlist? by remember { mutableStateOf(null) }

  val resources = LocalResources.current
  val snackbarState = remember { SnackbarHostState() }

  val filterSheetState = rememberModalBottomSheetState()
  var filterOpened: WishlistsFilter? by remember { mutableStateOf(null) }

  var isBackToPrivateDialogOpen by remember { mutableStateOf(false) }

  var isNewCategoryModalOpen by remember { mutableStateOf(false) }
  val newCategorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var areWishlistsFinishedVisible by remember { mutableStateOf(false) }
  val existsWishlistsFinished by remember(wishlists) {
    derivedStateOf {
      wishlists.any { w -> w.isFinished() }
    }
  }

  val wishlistInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isWishlistInfoModalOpen by remember { mutableStateOf(false) }

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
              isNewCategoryModalOpen = true
              collapse()
            }
          )
          FABMenuItem(
            icon = painterResource(R.drawable.ic_wishlists),
            text = stringResource(R.string.wishlists_wishlist),
            onClick = {
              onCreateWishlist(true)
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
        contentPadding = PaddingValues(bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        stickyHeader {
          WishlistsFiltersBar(
            modifier = Modifier
              .fillMaxWidth()
              .background(color = WishlifyTheme.colorScheme.surface),
            filtersState = uiState.filtersState,
            onOpenFilter = { filter -> filterOpened = filter },
            onUpdateState = onUpdateFilters
          )
        }

        items(
          items = wishlists.filter { !it.isFinished() },
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

        if (existsWishlistsFinished) {
          item(key = 1, contentType = "header") {
            SharedWishlistsFinishedHeader(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
              isVisible = areWishlistsFinishedVisible,
              description = htmlString(R.string.shared_wishlists_finished_header_own_description),
              onChangeVisibility = { areWishlistsFinishedVisible = !areWishlistsFinishedVisible }
            )
          }

          if (areWishlistsFinishedVisible) {
            items(
              items = wishlists.filter { it.isFinished() },
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

          WishlistsSettings.AdminCategories -> onAdminCategories()
        }
      }
    )

    wishlistSelected?.let { wishlist ->
      WishlistCardSettingsBottomSheet(
        visible = isWishlistSettingsModalOpen,
        sheetState = wishlistSettingsSheetState,
        settings = buildList {
          if (wishlist !is Wishlist.Shared) {
            add(WishlistCardSettings.Edit)
            if (wishlist.isShareable()) {
              add(WishlistCardSettings.Share)
            }
            add(WishlistCardSettings.Info)
            add(WishlistCardSettings.Delete)
          } else {
            add(WishlistCardSettings.Info)
            if (wishlist.event is Wishlist.SharedWishlistEvent && wishlist.isFinished()) {
              add(WishlistCardSettings.BackToPrivate)
            }
          }
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
              val isDialogVisible = wishlist.numOfNonPurchasedItems != wishlist.numOfItems
              if (isDialogVisible) {
                isShareInfoDialogVisible = true
              } else {
                onShareWishlist(wishlist)
              }
              coroutineScope
                .launch { wishlistSettingsSheetState.hide() }
                .invokeOnCompletion {
                  isWishlistSettingsModalOpen = false
                  if (!isDialogVisible) wishlistSelected = null
                }
            }

            WishlistCardSettings.Info -> {
              isWishlistInfoModalOpen = true
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

            WishlistCardSettings.BackToPrivate -> {
              isBackToPrivateDialogOpen = true
              coroutineScope
                .launch { wishlistSettingsSheetState.hide() }
                .invokeOnCompletion { isWishlistSettingsModalOpen = false }
            }
            else -> Unit
          }
        }
      )

      if (isBackToPrivateDialogOpen) {
        SharedWishlistMoveToPrivateDialog(
          onDismiss = {
            isBackToPrivateDialogOpen = false
            wishlistSelected = null
          },
          onConfirm = { onSharedBackToPrivate(wishlist) }
        )
      }

      WishlistInfoBottomSheet(
        visible = isWishlistInfoModalOpen,
        sheetState = wishlistInfoSheetState,
        wishlist = wishlist,
        onDismiss = {
          isWishlistInfoModalOpen = false
          wishlistSelected = null
        }
      )
    }

    filterOpened?.let { filter ->
      WishlistFilterBottomSheet(
        visible = true,
        sheetState = filterSheetState,
        filter = filter,
        categories = uiState.categories,
        onDismiss = { filterOpened = null },
        onApplyFilters = { f ->
          val filterState = uiState.filtersState
          val filterStateUpdated = when (f) {
            is WishlistsFilter.Availability ->
              filterState.copy(availability = f.takeUnless { it is WishlistsFilter.AvailabilityUnselected })

            is WishlistsFilter.Category ->
              filterState.copy(category = f.takeUnless { it is WishlistsFilter.CategoryUnselected })

            is WishlistsFilter.ShareStatus ->
              filterState.copy(shareStatus = f.takeUnless { it is WishlistsFilter.ShareStatusUnselected })

            is WishlistsFilter.Target ->
              filterState.copy(target = f.takeUnless { it is WishlistsFilter.TargetUnselected })

            else -> when (filter) {
              is WishlistsFilter.Target -> filterState.copy(target = null)
              is WishlistsFilter.Category -> filterState.copy(category = null)
              is WishlistsFilter.ShareStatus -> filterState.copy(shareStatus = null)
              is WishlistsFilter.Availability -> filterState.copy(availability = null)
            }
          }
          onUpdateFilters(filterStateUpdated)
          coroutineScope
            .launch { filterSheetState.hide() }
            .invokeOnCompletion { filterOpened = null }
        }
      )
    }

    uiState.wishlistNewItemByShare?.let { itemByShare ->
      WishlistSelectionBottomSheet(
        visible = uiState.isWishlistSelectionModalOpen,
        sheetState = wishlistSelectionSheetState,
        wishlists = wishlists.filter { it !is Wishlist.Shared },
        itemByShare = itemByShare,
        onDismiss = onCloseWishlistSelectionModal,
        onSelect = { w ->
          coroutineScope
            .launch { wishlistSelectionSheetState.hide() }
            .invokeOnCompletion {
              onCloseWishlistSelectionModal()
              onWishlistClickWithCreate(w, itemByShare)
            }
        }
      )
    }

    NewCategoryBottomSheet(
      isVisible = isNewCategoryModalOpen,
      sheetState = newCategorySheetState,
      error = uiState.newCategoryNameError,
      onClearInputError = onClearNewCategoryNameError,
      onDismiss = { isNewCategoryModalOpen = false },
      onCreate = { name, color ->
        coroutineScope
          .launch { newCategorySheetState.hide() }
          .invokeOnCompletion {
            isNewCategoryModalOpen = false
            onCreateCategory(name, color)
          }
      }
    )

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
  onCreateWishlist: (isOwn: Boolean) -> Unit,
  onCreateCategory: (name: String, color: Category.CategoryColor) -> Unit,
  onUpdateFilters: (WishlistsFiltersState) -> Unit,
  onClearSharedWishlistFeedback: () -> Unit,
  onClearNewCategoryNameError: () -> Unit,
  onAdminCategories: () -> Unit,
  onDismissError: () -> Unit,
) {

  val coroutineScope = rememberCoroutineScope()
  val filterSheetState = rememberModalBottomSheetState()
  var filterOpened: WishlistsFilter? by remember { mutableStateOf(null) }

  var isSettingsModalOpen by remember { mutableStateOf(false) }
  val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isNewCategoryModalOpen by remember { mutableStateOf(false) }
  val newCategorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
              isNewCategoryModalOpen = true
              collapse()
            }
          )
          FABMenuItem(
            icon = painterResource(R.drawable.ic_wishlists),
            text = stringResource(R.string.wishlists_wishlist),
            onClick = {
              onCreateWishlist(true)
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
          .padding(horizontal = 16.dp)
          .padding(bottom = 24.dp),
      ) {

        if (uiState.filtersState.hasFilters()) {
          WishlistsFiltersBar(
            modifier = Modifier
              .fillMaxWidth()
              .background(color = WishlifyTheme.colorScheme.surface),
            filtersState = uiState.filtersState,
            onOpenFilter = { filter -> filterOpened = filter },
            onUpdateState = onUpdateFilters
          )
        }

        Spacer(modifier = Modifier.height(80.dp))

        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.wishlists_list_empty_state),
          title = stringResource(R.string.wishlists_list_empty_state_title),
          description = when {
            uiState.filtersState.hasFilters() -> stringResource(R.string.wishlists_list_filters_empty_state_description)
            else -> stringResource(R.string.wishlists_list_own_empty_state_description)
          }
        )
      }
    }

    filterOpened?.let { filter ->
      WishlistFilterBottomSheet(
        visible = true,
        sheetState = filterSheetState,
        filter = filter,
        categories = uiState.categories,
        onDismiss = { filterOpened = null },
        onApplyFilters = { f ->
          val filterState = uiState.filtersState
          val filterStateUpdated = when (f) {
            is WishlistsFilter.Availability ->
              filterState.copy(availability = f.takeUnless { it is WishlistsFilter.AvailabilityUnselected })

            is WishlistsFilter.Category ->
              filterState.copy(category = f.takeUnless { it is WishlistsFilter.CategoryUnselected })

            is WishlistsFilter.ShareStatus ->
              filterState.copy(shareStatus = f.takeUnless { it is WishlistsFilter.ShareStatusUnselected })

            is WishlistsFilter.Target ->
              filterState.copy(target = f.takeUnless { it is WishlistsFilter.TargetUnselected })

            else -> when (filter) {
              is WishlistsFilter.Target -> filterState.copy(target = null)
              is WishlistsFilter.Category -> filterState.copy(category = null)
              is WishlistsFilter.ShareStatus -> filterState.copy(shareStatus = null)
              is WishlistsFilter.Availability -> filterState.copy(availability = null)
            }
          }
          onUpdateFilters(filterStateUpdated)
          coroutineScope
            .launch { filterSheetState.hide() }
            .invokeOnCompletion { filterOpened = null }
        }
      )
    }

    NewCategoryBottomSheet(
      isVisible = isNewCategoryModalOpen,
      sheetState = newCategorySheetState,
      error = uiState.newCategoryNameError,
      onClearInputError = onClearNewCategoryNameError,
      onDismiss = { isNewCategoryModalOpen = false },
      onCreate = { name, color ->
        coroutineScope
          .launch { newCategorySheetState.hide() }
          .invokeOnCompletion {
            isNewCategoryModalOpen = false
            onCreateCategory(name, color)
          }
      }
    )

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
fun WishlistsListLoadingScreen() {
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

        Loader(
          modifier = Modifier.weight(1f),
          containerColor = Color.Transparent
        )
      }
    }
  }
}
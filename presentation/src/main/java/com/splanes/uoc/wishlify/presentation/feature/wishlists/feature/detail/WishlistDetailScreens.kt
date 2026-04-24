package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ConfirmationDialog
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.filters.FilterProduct
import com.splanes.uoc.wishlify.presentation.common.components.filters.FilterProductBar
import com.splanes.uoc.wishlify.presentation.common.components.filters.FilterProductBottomSheet
import com.splanes.uoc.wishlify.presentation.common.utils.openBrowserLink
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.FABMenu
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.FABMenuItem
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.WishlistInfoBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.NewItemFromLinkBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.WishlistItemCard
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.WishlistItemDetailBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.WishlistItemSettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemForm
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistCardSettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistCardSettings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistDetailScreen(
  uiState: WishlistDetailUiState.Listing,
  onEditWishlist: (Wishlist) -> Unit,
  onDeleteWishlist: (Wishlist) -> Unit,
  onCreateItem: (link: String?) -> Unit,
  onItemAction: (WishlistItem, WishlistItemAction) -> Unit,
  onChangeProductFilters: (List<FilterProduct>) -> Unit,
  onChangeItemByLinkModalVisibility: (visible: Boolean) -> Unit,
  onClearInputError: (WishlistItemForm.Input) -> Unit,
  onCloseItemDetailModal: () -> Unit,
  onShare: () -> Unit,
  onBack: () -> Unit,
  onDismissError: () -> Unit
) {

  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val newItemByLinkSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isNewItemByLinkModalOpen by remember { mutableStateOf(false) }

  var isDeleteItemDialogVisible by remember { mutableStateOf(false) }
  var isDeleteWishlistDialogVisible by remember { mutableStateOf(false) }

  var isShareInfoDialogVisible by remember { mutableStateOf(false) }

  val itemSettingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isItemSettingsModalOpen by remember { mutableStateOf(false) }
  var itemSettingsSelected: WishlistItem? by remember { mutableStateOf(null) }

  val wishlistSettingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isWishlistSettingsModalOpen by remember { mutableStateOf(false) }

  val productFiltersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isProductFiltersModalOpen by remember { mutableStateOf(false) }

  val wishlistInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isWishlistInfoModalOpen by remember { mutableStateOf(false) }

  LaunchedEffect(uiState.isNewItemByLinkModalOpen) {
    if (uiState.isNewItemByLinkModalOpen) {
      isNewItemByLinkModalOpen = true
    } else {
      launch {
        newItemByLinkSheetState.hide()
      }.invokeOnCompletion {
        isNewItemByLinkModalOpen = false
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = uiState.wishlist.title) },
          navigationIcon = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onBack
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back)
              )
            }
          },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              enabled = uiState.isShareable(),
              onClick = {
                if (uiState.items.any { it.purchased != null }) {
                  isShareInfoDialogVisible = true
                } else {
                  onShare()
                }
              },
            ) {
              Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = stringResource(R.string.share)
              )
            }

            IconButton(
              shapes = IconButtonShape,
              onClick = { isWishlistSettingsModalOpen = true }
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
            icon = rememberVectorPainter(Icons.Outlined.Link),
            text = stringResource(R.string.wishlists_detail_link),
            onClick = {
              onChangeItemByLinkModalVisibility(true)
              collapse()
            }
          )
          FABMenuItem(
            icon = rememberVectorPainter(Icons.Outlined.Edit),
            text = stringResource(R.string.wishlists_detail_manually),
            onClick = {
              onCreateItem(null)
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
        contentPadding = PaddingValues(
          start = 16.dp,
          end = 16.dp,
          bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {

        if (uiState.productFilters.isNotEmpty()) {
          stickyHeader {
            FilterProductBar(
              modifier = Modifier.fillMaxWidth(),
              filters = uiState.productFilters,
              onOpenFilters = { isProductFiltersModalOpen = true },
              onChange = onChangeProductFilters
            )
          }
        }

        items(
          items = uiState.items,
          key = { item -> item.id }
        ) { item ->
          WishlistItemCard(
            modifier = Modifier.animateItem(),
            item = item,
            onSettingsClick = {
              itemSettingsSelected = item
              isItemSettingsModalOpen = true
            },
            onClick = { onItemAction(item, WishlistItemAction.Open) }
          )
        }
      }
    }

    WishlistInfoBottomSheet(
      visible = isWishlistInfoModalOpen,
      sheetState = wishlistInfoSheetState,
      wishlist = uiState.wishlist,
      onDismiss = { isWishlistInfoModalOpen = false }
    )

    FilterProductBottomSheet(
      visible = isProductFiltersModalOpen,
      sheetState = productFiltersSheetState,
      filters = listOf(
        FilterProduct.Filter.Price,
        FilterProduct.Filter.Priority,
      ),
      current = uiState.productFilters,
      onDismiss = {
        isProductFiltersModalOpen = false
      },
      onApply = { f ->
        coroutineScope
          .launch { productFiltersSheetState.hide() }
          .invokeOnCompletion {
            isProductFiltersModalOpen = false
            onChangeProductFilters(f)
          }
      }
    )

    WishlistCardSettingsBottomSheet(
      visible = isWishlistSettingsModalOpen,
      sheetState = wishlistSettingsSheetState,
      settings = listOf(
        WishlistCardSettings.FilterProducts,
        WishlistCardSettings.Edit,
        WishlistCardSettings.Info,
        WishlistCardSettings.Delete
      ),
      onDismiss = { isWishlistSettingsModalOpen = false },
      onSettingClick = { setting ->
        when (setting) {
          WishlistCardSettings.FilterProducts -> isProductFiltersModalOpen = true
          WishlistCardSettings.Edit -> onEditWishlist(uiState.wishlist)
          WishlistCardSettings.Info -> isWishlistInfoModalOpen = true
          WishlistCardSettings.Delete -> isDeleteWishlistDialogVisible = true
          else -> {
            // Not allowed other settings
          }
        }
        coroutineScope
          .launch { wishlistSettingsSheetState.hide() }
          .invokeOnCompletion { isWishlistSettingsModalOpen = false }
      }
    )

    itemSettingsSelected?.let { item ->
      WishlistItemSettingsBottomSheet(
        visible = isItemSettingsModalOpen,
        sheetState = itemSettingsSheetState,
        settings = listOfNotNull(
          WishlistItemAction.Edit,
          WishlistItemAction.TogglePurchase,
          WishlistItemAction.OpenLink.takeIf { item.link.isNotBlank() },
          WishlistItemAction.Delete
        ),
        purchased = item.purchased != null,
        onDismiss = {
          isItemSettingsModalOpen = false
          itemSettingsSelected = null
        },
        onSettingClick = { action ->
          coroutineScope
            .launch { itemSettingsSheetState.hide() }
            .invokeOnCompletion { isItemSettingsModalOpen = false }
          when (action) {
            WishlistItemAction.Open -> {
              // Action not allowed
            }
            WishlistItemAction.Delete -> {
              isDeleteItemDialogVisible = true
            }
            WishlistItemAction.Edit,
            WishlistItemAction.TogglePurchase -> {
              onItemAction(item, action)
              itemSettingsSelected = null
            }
            WishlistItemAction.OpenLink -> {
              context.openBrowserLink(item.link)
              itemSettingsSelected = null
            }
          }
        }
      )
    }

    NewItemFromLinkBottomSheet(
      visible = isNewItemByLinkModalOpen,
      sheetState = newItemByLinkSheetState,
      error = uiState.newItemByLinkError,
      onClearInputError = { onClearInputError(WishlistItemForm.Input.Link) },
      onCreate = { link ->
        onChangeItemByLinkModalVisibility(false)
        onCreateItem(link)
      },
      onDismiss = { onChangeItemByLinkModalVisibility(false) }
    )

    uiState.itemSelected?.let { item ->
      WishlistItemDetailBottomSheet(
        visible = uiState.isItemDetailModalOpen,
        sheetState = detailSheetState,
        item = item,
        isButtonLoading = uiState.isItemDetailButtonLoading,
        onDismiss = {
          coroutineScope
            .launch { detailSheetState.hide() }
            .invokeOnCompletion { onCloseItemDetailModal() }
        },
        onAction = { action ->
          when (action) {
            WishlistItemAction.Delete -> isDeleteItemDialogVisible = true
            WishlistItemAction.OpenLink -> {
              val opened = context.openBrowserLink(item.link)
              if (opened) {
                coroutineScope
                  .launch { detailSheetState.hide() }
                  .invokeOnCompletion { onCloseItemDetailModal() }
              }
            }

            else -> onItemAction(item, action)
          }
        }
      )
    }

    if (isShareInfoDialogVisible) {
      ConfirmationDialog(
        icon = Icons.Rounded.QuestionMark,
        title = stringResource(R.string.error_dialog_title_warning),
        description = stringResource(R.string.wishlists_share_wishlist_with_purchased_items_dialog),
        onDismiss = { isShareInfoDialogVisible = false },
        onConfirm = onShare
      )
    }

    if (isDeleteItemDialogVisible) {
      ConfirmationDialog(
        onDismiss = { isDeleteItemDialogVisible = false },
        onConfirm = {
          val itemSelected = uiState.itemSelected ?: itemSettingsSelected
          itemSelected?.let { item ->
            onItemAction(item, WishlistItemAction.Delete)
            itemSettingsSelected = null
          }
        }
      )
    }

    if (isDeleteWishlistDialogVisible) {
      ConfirmationDialog(
        onDismiss = { isDeleteWishlistDialogVisible = false },
        onConfirm = { onDeleteWishlist(uiState.wishlist) }
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
fun WishlistDetailEmptyScreen(
  uiState: WishlistDetailUiState.Empty,
  onEditWishlist: (Wishlist) -> Unit,
  onDeleteWishlist: (Wishlist) -> Unit,
  onCreateItem: (link: String?) -> Unit,
  onBack: () -> Unit,
  onChangeProductFilters: (List<FilterProduct>) -> Unit,
  onChangeItemByLinkModalVisibility: (visible: Boolean) -> Unit,
  onClearInputError: (WishlistItemForm.Input) -> Unit,
  onDismissError: () -> Unit
) {
  val coroutineScope = rememberCoroutineScope()

  val newItemByLinkSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isNewItemByLinkModalOpen by remember { mutableStateOf(false) }

  val wishlistSettingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isWishlistSettingsModalOpen by remember { mutableStateOf(false) }

  var isDeleteWishlistDialogVisible by remember { mutableStateOf(false) }

  val productFiltersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isProductFiltersModalOpen by remember { mutableStateOf(false) }

  val wishlistInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isWishlistInfoModalOpen by remember { mutableStateOf(false) }

  LaunchedEffect(uiState.isNewItemByLinkModalOpen) {
    if (uiState.isNewItemByLinkModalOpen) {
      isNewItemByLinkModalOpen = true
    } else {
      launch {
        newItemByLinkSheetState.hide()
      }.invokeOnCompletion {
        isNewItemByLinkModalOpen = false
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = uiState.wishlistName) },
          navigationIcon = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onBack
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back)
              )
            }
          },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = { isWishlistSettingsModalOpen = true }
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
            icon = rememberVectorPainter(Icons.Outlined.Link),
            text = stringResource(R.string.wishlists_detail_link),
            onClick = {
              onChangeItemByLinkModalVisibility(true)
              collapse()
            }
          )
          FABMenuItem(
            icon = rememberVectorPainter(Icons.Outlined.Edit),
            text = stringResource(R.string.wishlists_detail_manually),
            onClick = {
              onCreateItem(null)
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
        verticalArrangement = Arrangement.Center
      ) {

        if (uiState.productFilters.isNotEmpty()) {
          FilterProductBar(
            modifier = Modifier.fillMaxWidth(),
            filters = uiState.productFilters,
            onOpenFilters = { isProductFiltersModalOpen = true },
            onChange = onChangeProductFilters
          )
        }

        Spacer(Modifier.weight(.5f))

        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.wishlists_items_empty_state),
          title = stringResource(R.string.wishlists_detail_empty_state_title),
          description = if (uiState.productFilters.isEmpty()) {
            stringResource(R.string.wishlists_detail_empty_state_description)
          } else {
            stringResource(R.string.wishlists_detail_empty_state_with_filters_description)
          }
        )

        Spacer(Modifier.weight(1f))

      }
    }

    WishlistInfoBottomSheet(
      visible = isWishlistInfoModalOpen,
      sheetState = wishlistInfoSheetState,
      wishlist = uiState.wishlist,
      onDismiss = { isWishlistInfoModalOpen = false }
    )

    FilterProductBottomSheet(
      visible = isProductFiltersModalOpen,
      sheetState = productFiltersSheetState,
      filters = listOf(
        FilterProduct.Filter.Price,
        FilterProduct.Filter.Priority,
      ),
      current = uiState.productFilters,
      onDismiss = { isProductFiltersModalOpen = false },
      onApply = { f ->
        coroutineScope
          .launch { productFiltersSheetState.hide() }
          .invokeOnCompletion {
            isProductFiltersModalOpen = false
            onChangeProductFilters(f)
          }
      }
    )

    WishlistCardSettingsBottomSheet(
      visible = isWishlistSettingsModalOpen,
      sheetState = wishlistSettingsSheetState,
      settings = listOfNotNull(
        if (uiState.productFilters.isNotEmpty()) {
          WishlistCardSettings.FilterProducts
        } else {
          null
        },
        WishlistCardSettings.Edit,
        WishlistCardSettings.Info,
        WishlistCardSettings.Delete
      ),
      onDismiss = { isWishlistSettingsModalOpen = false },
      onSettingClick = { setting ->
        when (setting) {
          WishlistCardSettings.FilterProducts -> isProductFiltersModalOpen = true
          WishlistCardSettings.Edit -> onEditWishlist(uiState.wishlist)
          WishlistCardSettings.Info -> isWishlistInfoModalOpen = true
          WishlistCardSettings.Delete -> isDeleteWishlistDialogVisible = true
          else -> {
            // Not allowed other settings
          }
        }
        coroutineScope
          .launch { wishlistSettingsSheetState.hide() }
          .invokeOnCompletion { isWishlistSettingsModalOpen = false }
      }
    )

    NewItemFromLinkBottomSheet(
      visible = isNewItemByLinkModalOpen,
      sheetState = newItemByLinkSheetState,
      error = uiState.newItemByLinkError,
      onClearInputError = { onClearInputError(WishlistItemForm.Input.Link) },
      onCreate = { link ->
        onChangeItemByLinkModalVisibility(false)
        onCreateItem(link)
      },
      onDismiss = { onChangeItemByLinkModalVisibility(false) }
    )

    if (isDeleteWishlistDialogVisible) {
      ConfirmationDialog(
        onDismiss = { isDeleteWishlistDialogVisible = false },
        onConfirm = { onDeleteWishlist(uiState.wishlist) }
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
fun WishlistDetailLoadingScreen(
  uiState: WishlistDetailUiState.Loading,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = uiState.wishlistName) },
        navigationIcon = {
          IconButton(
            shapes = IconButtonShape,
            onClick = onBack
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
        },
      )
    },
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
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistDetailErrorScreen(
  uiState: WishlistDetailUiState.Error,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = uiState.wishlistName) },
        navigationIcon = {
          IconButton(
            shapes = IconButtonShape,
            onClick = onBack
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
        },
      )
    },
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

      Spacer(Modifier.weight(.5f))

      // Used as error component as well
      EmptyState(
        modifier = Modifier.fillMaxWidth(),
        image = painterResource(R.drawable.generic_error),
        title = stringResource(R.string.wishlists_detail_error_title),
        description = stringResource(R.string.wishlists_detail_error_description)
      )

      Spacer(Modifier.weight(1f))
    }
  }
}

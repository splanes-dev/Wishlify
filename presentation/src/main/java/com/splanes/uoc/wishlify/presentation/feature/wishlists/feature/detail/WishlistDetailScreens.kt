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
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ConfirmationDialog
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.utils.openBrowserLink
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.FABMenu
import com.splanes.uoc.wishlify.presentation.feature.wishlists.components.FABMenuItem
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.NewItemFromLinkBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.WishlistItemCard
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.WishlistItemDetailBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemForm
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistDetailScreen(
  uiState: WishlistDetailUiState.Listing,
  onCreateItem: (link: String?) -> Unit,
  onItemAction: (WishlistItem, WishlistItemAction) -> Unit,
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
              onClick = onShare
            ) {
              Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = stringResource(R.string.share)
              )
            }

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
          horizontal = 16.dp,
          vertical = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        items(
          items = uiState.items,
          key = { item -> item.id }
        ) { item ->
          WishlistItemCard(
            modifier = Modifier.animateItem(),
            item = item,
            onSettingsClick = { /* TODO */ },
            onClick = { onItemAction(item, WishlistItemAction.Open) }
          )
        }
      }
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

    if (isDeleteItemDialogVisible) {
      ConfirmationDialog(
        onDismiss = { isDeleteItemDialogVisible = false },
        onConfirm = {
          uiState.itemSelected?.let { item ->
            onItemAction(item, WishlistItemAction.Delete)
          }
        }
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
  onCreateItem: (link: String?) -> Unit,
  onBack: () -> Unit,
  onShare: () -> Unit,
  onChangeItemByLinkModalVisibility: (visible: Boolean) -> Unit,
  onClearInputError: (WishlistItemForm.Input) -> Unit,
  onDismissError: () -> Unit
) {
  val newItemByLinkSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isNewItemByLinkModalOpen by remember { mutableStateOf(false) }

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
              onClick = onShare
            ) {
              Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = stringResource(R.string.share)
              )
            }

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
          .padding(
            horizontal = 16.dp,
            vertical = 24.dp
          ),
        verticalArrangement = Arrangement.Center
      ) {

        Spacer(Modifier.weight(.5f))

        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.wishlists_items_empty_state),
          title = stringResource(R.string.wishlists_detail_empty_state_title),
          description = stringResource(R.string.wishlists_detail_empty_state_description)
        )

        Spacer(Modifier.weight(1f))

      }
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

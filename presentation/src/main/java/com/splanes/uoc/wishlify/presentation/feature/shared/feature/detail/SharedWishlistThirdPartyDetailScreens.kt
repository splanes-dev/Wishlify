package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail

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
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.utils.openBrowserLink
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.components.SharedWishlistHeader
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.components.SharedWishlistItemCardAnimated
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.components.SharedWishlistItemDetailBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.components.SharedWishlistItemInfoBanner
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.components.SharedWishlistItemStateBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.model.SharedWishlistItemAction
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistThirdPartyDetailScreen(
  uiState: SharedWishlistThirdPartyDetailUiState.Listing,
  onItemAction: (item: SharedWishlistItem, action: SharedWishlistItemAction) -> Unit,
  onUpdateItemState: (item: SharedWishlistItem, action: SharedWishlistItemAction.UpdateState) -> Unit,
  onOpenItemStateModal: (item: SharedWishlistItem) -> Unit,
  onChatClick: () -> Unit,
  onCloseItemDetailModal: () -> Unit,
  onCloseItemStateModal: () -> Unit,
  onClearShareRequestError: () -> Unit,
  onDismissBanner: () -> Unit,
  onBack: () -> Unit,
  onDismissError: () -> Unit,
) {

  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val itemStateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            if (uiState.target.isNotBlank()) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = uiState.wishlistName)
                Text(text = uiState.target, style = WishlifyTheme.typography.bodySmall)
              }
            } else {
              Text(text = uiState.wishlistName)
            }
          },
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
              onClick = { /* todo */ }
            ) {
              Icon(
                imageVector = Icons.Outlined.FilterAlt,
                contentDescription = stringResource(R.string.filter)
              )
            }
          }
        )
      },
      floatingActionButton = {
        if (!uiState.wishlist.isFinished()) {
          // TODO: Badge
          Box {
            FloatingActionButton(
              shape = WishlifyTheme.shapes.medium,
              containerColor = WishlifyTheme.colorScheme.tertiaryContainer,
              contentColor = WishlifyTheme.colorScheme.onTertiaryContainer,
              onClick = onChatClick,
            ) {
              Text(
                text = stringResource(R.string.chat),
                style = WishlifyTheme.typography.titleMedium,
              )
            }
          }
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
        stickyHeader {
          SharedWishlistHeader(
            modifier = Modifier.fillMaxWidth(),
            group = uiState.wishlist.group,
            participantsCount = uiState.wishlist.participants.count(),
            itemsAvailableCount = uiState.items.count { it.state == SharedWishlistItem.Available },
            deadline = uiState.wishlist.deadline
          )
        }

        if (uiState.isInfoBannerVisible && !uiState.wishlist.isFinished()) {
          item(key = "banner") {
            SharedWishlistItemInfoBanner(
              modifier = Modifier
                .fillMaxWidth()
                .animateItem(),
              onDismiss = onDismissBanner
            )
          }
        }

        items(
          items = uiState.items,
          key = { item -> item.id }
        ) { item ->
          SharedWishlistItemCardAnimated(
            modifier = Modifier
              .fillMaxWidth()
              .animateItem(),
            item = item,
            onClick = { onItemAction(item, SharedWishlistItemAction.Open) },
            onSettingsClick = { onOpenItemStateModal(item) }
          )
        }
      }
    }

    uiState.itemSelected?.let { item ->
      SharedWishlistItemDetailBottomSheet(
        visible = uiState.isItemDetailModalOpen,
        sheetState = detailSheetState,
        item = item,
        itemStateActions = uiState.itemStateActions,
        isButtonLoading = uiState.isItemDetailButtonLoading,
        shareRequestError = uiState.shareRequestError,
        onClearShareRequestError = onClearShareRequestError,
        onDismiss = {
          coroutineScope
            .launch { detailSheetState.hide() }
            .invokeOnCompletion { onCloseItemDetailModal() }
        },
        onAction = { action ->
          when (action) {
            SharedWishlistItemAction.OpenLink -> {
              val opened = context.openBrowserLink(item.linkedItem.link)
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

    uiState.itemSelectedToUpdateState?.let { item ->
      SharedWishlistItemStateBottomSheet(
        visible = uiState.isWishlistItemStateModalOpen,
        sheetState = itemStateSheetState,
        item = item,
        shareRequestError = uiState.shareRequestError,
        onClearShareRequestError = onClearShareRequestError,
        onDismiss = {
          coroutineScope
            .launch { detailSheetState.hide() }
            .invokeOnCompletion { onCloseItemStateModal() }
        },
        onUpdateState = { action -> onUpdateItemState(item, action) }
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
fun SharedWishlistThirdPartyDetailLoadingScreen(
  uiState: SharedWishlistThirdPartyDetailUiState.Loading,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          if (uiState.target.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(text = uiState.wishlistName)
              Text(text = uiState.target, style = WishlifyTheme.typography.bodySmall)
            }
          } else {
            Text(text = uiState.wishlistName)
          }
        },
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
fun SharedWishlistThirdPartyDetailErrorScreen(
  uiState: SharedWishlistThirdPartyDetailUiState.Error,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          if (uiState.target.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(text = uiState.wishlistName)
              Text(text = uiState.target, style = WishlifyTheme.typography.bodySmall)
            }
          } else {
            Text(text = uiState.wishlistName)
          }
        },
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
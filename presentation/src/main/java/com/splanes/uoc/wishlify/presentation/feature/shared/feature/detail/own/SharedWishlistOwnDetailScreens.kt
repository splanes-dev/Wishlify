package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own

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
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own.components.SharedOwnWishlistDetailSettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own.components.SharedOwnWishlistHeader
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own.components.SharedOwnWishlistItemCard
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own.components.SharedOwnWishlistItemDetailBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own.model.SharedOwnWishlistSettings
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components.SharedWishlistMoveToPrivateDialog
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistOwnDetailScreen(
  uiState: SharedWishlistOwnDetailUiState.Listing,
  onOpenItemDetail: (SharedWishlistItem) -> Unit,
  onCloseItemDetailModal: () -> Unit,
  onBackToPrivates: () -> Unit,
  onDismissError: () -> Unit,
  onBack: () -> Unit,
) {

  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isSettingsModalOpen by remember { mutableStateOf(false) }
  val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isBackToPrivateDialogOpen by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            if (uiState.wishlistTarget.isNotBlank()) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = uiState.wishlistName)
                Text(text = uiState.wishlistTarget, style = WishlifyTheme.typography.bodySmall)
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
            if (uiState.wishlist.isFinished()) {
              IconButton(
                shapes = IconButtonShape,
                onClick = { isSettingsModalOpen = true }
              ) {
                Icon(
                  imageVector = Icons.Outlined.Tune,
                  contentDescription = stringResource(R.string.settings)
                )
              }
            } else {
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
          }
        )
      },
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
          SharedOwnWishlistHeader(
            modifier = Modifier.fillMaxWidth(),
            group = uiState.wishlist.group,
            participantsCount = uiState.wishlist.participants.count(),
            deadline = uiState.wishlist.deadline
          )
        }

        items(
          items = uiState.items,
          key = { item -> item.id }
        ) { item ->
          SharedOwnWishlistItemCard(
            modifier = Modifier
              .fillMaxWidth()
              .animateItem(),
            item = item,
            onClick = { onOpenItemDetail(item) }
          )
        }
      }
    }

    uiState.itemSelected?.let { item ->
      SharedOwnWishlistItemDetailBottomSheet(
        visible = uiState.isItemDetailModalOpen,
        sheetState = detailSheetState,
        item = item,
        onDismiss = onCloseItemDetailModal,
        onOpenLink = {
          val opened = context.openBrowserLink(item.linkedItem.link)
          if (opened) {
            coroutineScope
              .launch { detailSheetState.hide() }
              .invokeOnCompletion { onCloseItemDetailModal() }
          }
        }
      )
    }

    SharedOwnWishlistDetailSettingsBottomSheet(
      visible = isSettingsModalOpen,
      sheetState = settingsSheetState,
      settings = SharedOwnWishlistSettings.entries,
      onDismiss = { isSettingsModalOpen = false },
      onSettingClick = { setting ->
        when (setting) {
          SharedOwnWishlistSettings.Filter -> {
            // TODO
          }
          SharedOwnWishlistSettings.BackToPrivates -> isBackToPrivateDialogOpen = true
        }
        coroutineScope
          .launch { settingsSheetState.hide() }
          .invokeOnCompletion { isSettingsModalOpen = false }
      }
    )

    if (isBackToPrivateDialogOpen) {
      SharedWishlistMoveToPrivateDialog(
        onDismiss = { isBackToPrivateDialogOpen = false },
        onConfirm = { onBackToPrivates() }
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
fun SharedWishlistOwnDetailLoadingScreen(
  uiState: SharedWishlistOwnDetailUiState.Loading,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          if (uiState.wishlistTarget.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(text = uiState.wishlistName)
              Text(text = uiState.wishlistTarget, style = WishlifyTheme.typography.bodySmall)
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
fun SharedWishlistOwnDetailErrorScreen(
  uiState: SharedWishlistOwnDetailUiState.Error,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          if (uiState.wishlistTarget.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(text = uiState.wishlistName)
              Text(text = uiState.wishlistTarget, style = WishlifyTheme.typography.bodySmall)
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
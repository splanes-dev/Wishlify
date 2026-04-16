package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.share

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ConfirmationDialog
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.utils.openBrowserLink
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.share.components.SecretSantaShareWishlistBanner
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.WishlistItemCard
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.WishlistItemDetailBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.WishlistCard
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaShareWishlistScreen(
  uiState: SecretSantaShareWishlistUiState.Wishlists,
  onShareWishlist: (Wishlist.Own) -> Unit,
  onOpenWishlist: (Wishlist.Own) -> Unit,
  onSelectWishlist: (Wishlist.Own?) -> Unit,
  onCancel: () -> Unit,
) {
  var isShareInfoDialogVisible by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            Text(text = stringResource(R.string.secret_santa_share_wishlist_title))
          },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onCancel
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.cancel)
              )
            }
          }
        )
      },
    ) { paddings ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddings)
          .padding(
            horizontal = 16.dp,
            vertical = 24.dp
          ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

          item {
            SecretSantaShareWishlistBanner(Modifier.fillMaxWidth())
          }

          items(
            items = uiState.wishlists,
            key = { item -> item.id }
          ) { wishlist ->

            val selected = wishlist.id == uiState.wishlistSelected?.id

            val surfaceColor by animateColorAsState(
              targetValue = if (selected) {
                WishlifyTheme.colorScheme.primaryContainer.copy(alpha = .4f)
              } else {
                WishlifyTheme.colorScheme.surface
              }
            )

            val scaleY by animateFloatAsState(
              targetValue = if (selected) .85f else 1f
            )

            val scaleX by animateFloatAsState(
              targetValue = if (selected) .9f else 1f
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Surface(
                modifier = Modifier.weight(1f),
                shape = WishlifyTheme.shapes.small,
                color = surfaceColor
              ) {
                WishlistCard(
                  modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                      this.scaleX = scaleX
                      this.scaleY = scaleY
                    },
                  wishlist = wishlist,
                  onSettingsClick = null, // No settings allowed here
                  onClick = { onOpenWishlist(wishlist) }
                )
              }

              Checkbox(
                checked = selected,
                onCheckedChange = { checked -> onSelectWishlist(wishlist.takeIf { checked }) },
              )
            }
          }
        }

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = uiState.wishlistSelected != null,
          onClick = {
            uiState.wishlistSelected?.let { wishlist ->
              if (wishlist.numOfItems != wishlist.numOfNonPurchasedItems) {
                isShareInfoDialogVisible = true
              } else {
                onShareWishlist(wishlist)
              }
            }
          }
        ) {
          ButtonText(text = stringResource(R.string.share))
        }
      }
    }

    if (isShareInfoDialogVisible) {
      ConfirmationDialog(
        icon = Icons.Rounded.QuestionMark,
        title = stringResource(R.string.error_dialog_title_warning),
        description = stringResource(R.string.wishlists_share_wishlist_with_purchased_items_dialog),
        onDismiss = { isShareInfoDialogVisible = false },
        onConfirm = { uiState.wishlistSelected?.run(onShareWishlist) }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaShareWishlistDetailScreen(
  uiState: SecretSantaShareWishlistUiState.WishlistDetail,
  onOpenItemDetailModal: (WishlistItem) -> Unit,
  onCloseItemDetailModal: () -> Unit,
  onBack: () -> Unit,
  onCancel: () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            Text(text = uiState.wishlist.title)
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
              onClick = onCancel
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.cancel)
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
        contentPadding = PaddingValues(
          vertical = 24.dp,
          horizontal = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {

        stickyHeader {
          SecretSantaShareWishlistBanner(modifier = Modifier.fillMaxWidth())
        }

        items(
          items = uiState.items,
          key = { item -> item.id }
        ) { item ->
          WishlistItemCard(
            modifier = Modifier.animateItem(),
            item = item,
            onSettingsClick = null, // No settings allowed here
            onClick = { onOpenItemDetailModal(item) }
          )
        }
      }
    }
  }

  uiState.itemSelected?.let { item ->
    WishlistItemDetailBottomSheet(
      visible = uiState.isDetailModalOpen,
      sheetState = detailSheetState,
      item = item,
      isButtonLoading = false,
      onDismiss = {
        coroutineScope
          .launch { detailSheetState.hide() }
          .invokeOnCompletion { onCloseItemDetailModal() }
      },
      readOnly = true,
      onAction = { action ->
        when (action) {
          WishlistItemAction.OpenLink -> {
            val opened = context.openBrowserLink(item.link)
            if (opened) {
              coroutineScope
                .launch { detailSheetState.hide() }
                .invokeOnCompletion { onCloseItemDetailModal() }
            }
          }

          else -> {
            // Should not happen
          }
        }
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaShareWishlistLoadingScreen(
  uiState: SecretSantaShareWishlistUiState.Loading,
  onBack: () -> Unit,
  onCancel: () -> Unit
) {
  BackHandler(enabled = uiState.hasBack) {
    onBack()
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            val text = uiState.wishlist?.title
              ?: stringResource(R.string.secret_santa_share_wishlist_title)
            Text(text = text)
          },
          navigationIcon = {
            if (uiState.hasBack) {
              IconButton(
                shapes = IconButtonShape,
                onClick = onBack
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                  contentDescription = stringResource(R.string.back)
                )
              }
            }
          },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onCancel
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.cancel)
              )
            }
          }
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
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaShareWishlistEmptyScreen(
  uiState: SecretSantaShareWishlistUiState.Empty,
  onBack: () -> Unit,
  onCancel: () -> Unit
) {

  BackHandler(enabled = uiState.hasBack) {
    onBack()
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            val text = uiState.wishlist?.title
              ?: stringResource(R.string.secret_santa_share_wishlist_title)
            Text(text = text)
          },
          navigationIcon = {
            if (uiState.hasBack) {
              IconButton(
                shapes = IconButtonShape,
                onClick = onBack
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                  contentDescription = stringResource(R.string.back)
                )
              }
            }
          },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onCancel
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.cancel)
              )
            }
          }
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

        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = if (uiState.wishlist != null) {
            R.drawable.wishlists_items_empty_state
          } else {
            R.drawable.wishlists_list_empty_state
          }.let { id -> painterResource(id) },
          title = if (uiState.wishlist != null) {
            R.string.wishlists_detail_empty_state_title
          } else {
            R.string.wishlists_list_empty_state_title
          }.let { id -> stringResource(id) },
          description = if (uiState.wishlist != null) {
            R.string.wishlists_detail_empty_state_description
          } else {
            R.string.wishlists_list_own_empty_state_description
          }.let { id -> stringResource(id) },
        )
      }
    }
  }
}
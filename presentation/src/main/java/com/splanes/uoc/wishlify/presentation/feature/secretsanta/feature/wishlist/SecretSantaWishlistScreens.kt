package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.wishlist

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ConfirmationDialog
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.utils.openBrowserLink
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.wishlist.components.SecretSantaWishlistInfoBanner
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.wishlist.model.SecretSantaWishlistAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.WishlistItemCard
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.WishlistItemDetailBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemAction
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaWishlistScreen(
  uiState: SecretSantaWishlistUiState.Listing,
  onOpenItemDetailModal: (WishlistItem) -> Unit,
  onCloseItemDetailModal: () -> Unit,
  onEditWishlist: () -> Unit,
  onDeleteWishlist: () -> Unit,
  onCancel: () -> Unit,
) {

  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var wishlistActionSelected: SecretSantaWishlistAction? by remember { mutableStateOf(null) }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            Text(text = uiState.wishlist.title)
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
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            SecretSantaWishlistInfoBanner(
              modifier = Modifier.fillMaxWidth(),
              isOwnWishlist = uiState.isOwnWishlist
            )

            if (uiState.isOwnWishlist) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedIconButton(
                  shapes = IconButtonShape,
                  border = BorderStroke(width = 1.dp, color =  WishlifyTheme.colorScheme.error),
                  colors = IconButtonDefaults.outlinedIconButtonColors(
                    contentColor = WishlifyTheme.colorScheme.error
                  ),
                  onClick = { wishlistActionSelected = SecretSantaWishlistAction.Delete }
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete),
                  )
                }

                OutlinedButton(
                  shapes = ButtonShape,
                  border = BorderStroke(width = 1.dp, color =  WishlifyTheme.colorScheme.primary),
                  colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = WishlifyTheme.colorScheme.primary
                  ),
                  onClick = { wishlistActionSelected = SecretSantaWishlistAction.Edit }
                ) {
                  Text(
                    text = stringResource(R.string.secret_santa_wishlist_change_wishlist_btn)
                  )
                }
              }
            }
          }
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

    wishlistActionSelected?.let { action ->
      ConfirmationDialog(
        description = when (action) {
          SecretSantaWishlistAction.Edit -> R.string.secret_santa_wishlist_action_dialog_description_edit
          SecretSantaWishlistAction.Delete -> R.string.secret_santa_wishlist_action_dialog_description_delete
        }.let { id -> stringResource(id) },
        onDismiss = { wishlistActionSelected = null },
        onConfirm = {
          when (action) {
            SecretSantaWishlistAction.Edit -> onEditWishlist()
            SecretSantaWishlistAction.Delete -> onDeleteWishlist()
          }
        }
      )
    }

    uiState.itemSelected?.let { item ->
      WishlistItemDetailBottomSheet(
        visible = uiState.isItemDetailOpened,
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
}

@Composable
fun SecretSantaWishlistLoadingScreen(
  uiState: SecretSantaWishlistUiState.Loading,
  onCancel: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            val text = uiState.wishlistName
              ?: stringResource(R.string.secret_santa_shared_wishlist_title)
            Text(text = text)
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

@Composable
fun SecretSantaWishlistErrorScreen(
  uiState: SecretSantaWishlistUiState.Error,
  onCancel: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            val text = uiState.wishlistName
              ?: stringResource(R.string.secret_santa_shared_wishlist_title)
            Text(text = text)
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

        // Used as error component as well
        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.generic_error),
          title = stringResource(R.string.wishlists_detail_error_title),
          description = stringResource(R.string.wishlists_detail_error_description)
        )
      }
    }
  }
}
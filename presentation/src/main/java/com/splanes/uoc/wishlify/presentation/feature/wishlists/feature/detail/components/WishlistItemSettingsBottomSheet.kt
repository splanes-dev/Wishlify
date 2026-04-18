package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.RotateLeft
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemAction
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistItemSettingsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  settings: List<WishlistItemAction>,
  purchased: Boolean,
  onDismiss: () -> Unit,
  onSettingClick: (WishlistItemAction) -> Unit,
) {
  SettingsBottomSheet(
    visible = visible,
    sheetState = sheetState,
    settings = settings.map { setting ->
      SettingsBottomSheet.Option(
        id = setting.name,
        icon = setting.icon(purchased),
        text = setting.text(purchased),
        contentColor = WishlifyTheme.colorScheme.error.takeIf { setting == WishlistItemAction.Delete }
      )
    },
    onDismiss = onDismiss,
    onSettingClick = { option ->
      val setting = WishlistItemAction.valueOf(option.id)
      onSettingClick(setting)
    }
  )
}

private fun WishlistItemAction.icon(purchased: Boolean) = when (this) {
  WishlistItemAction.Delete -> Icons.Outlined.DeleteForever
  WishlistItemAction.Edit -> Icons.Outlined.BorderColor
  WishlistItemAction.TogglePurchase -> if (purchased) {
    Icons.AutoMirrored.Outlined.RotateLeft
  } else {
    Icons.Outlined.LocalMall
  }

  WishlistItemAction.OpenLink -> Icons.Rounded.Link
  else -> {
    // No other actions allowed
    error("Wishlist item action not allowed: $this")
  }
}

@Composable
private fun WishlistItemAction.text(purchased: Boolean) = when (this) {
  WishlistItemAction.Delete -> R.string.wishlists_item_delete_item
  WishlistItemAction.Edit -> R.string.wishlists_edit_item_title
  WishlistItemAction.TogglePurchase -> if (purchased) {
    R.string.wishlists_item_mark_as_not_purchased
  } else {
    R.string.wishlists_item_mark_as_purchased
  }

  WishlistItemAction.OpenLink -> R.string.wishlists_open_link_item_title
  else -> {
    // No other actions allowed
    error("Wishlist item action not allowed: $this")
  }
}.let { id -> stringResource(id) }
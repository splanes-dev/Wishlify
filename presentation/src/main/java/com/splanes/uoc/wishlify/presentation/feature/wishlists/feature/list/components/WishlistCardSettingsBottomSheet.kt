package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistCardSettings
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistCardSettingsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  settings: List<WishlistCardSettings> = WishlistCardSettings.entries,
  onDismiss: () -> Unit,
  onSettingClick: (WishlistCardSettings) -> Unit,
) {
  SettingsBottomSheet(
    visible = visible,
    sheetState = sheetState,
    settings = settings.map { setting ->
      SettingsBottomSheet.Option(
        id = setting.name,
        icon = setting.icon(),
        text = setting.text(),
        contentColor = if (setting == WishlistCardSettings.Delete) {
          WishlifyTheme.colorScheme.error
        } else {
          null
        }
      )
    },
    onDismiss = onDismiss,
    onSettingClick = { option ->
      val setting = WishlistCardSettings.valueOf(option.id)
      onSettingClick(setting)
    }
  )
}

private fun WishlistCardSettings.icon() = when (this) {
  WishlistCardSettings.Edit -> Icons.Outlined.BorderColor
  WishlistCardSettings.Share -> Icons.Outlined.Share
  WishlistCardSettings.Delete -> Icons.Outlined.DeleteForever
}

@Composable
private fun WishlistCardSettings.text() = when (this) {
  WishlistCardSettings.Edit -> R.string.wishlists_item_edit
  WishlistCardSettings.Share -> R.string.wishlists_item_share
  WishlistCardSettings.Delete -> R.string.wishlists_item_delete
}.let { id -> stringResource(id) }
package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.model.SharedWishlistsSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedWishlistSettingsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  settings: List<SharedWishlistsSettings>,
  onDismiss: () -> Unit,
  onSettingClick: (SharedWishlistsSettings) -> Unit
) {
  SettingsBottomSheet(
    visible = visible,
    sheetState = sheetState,
    settings = settings.mapIndexed { index, setting ->
      SettingsBottomSheet.Option(
        id = index.toString(),
        icon = setting.icon(),
        text = setting.text(),
        contentColor = setting.color()
      )
    },
    onDismiss = onDismiss,
    onSettingClick = { option ->
      onSettingClick(settings[option.id.toInt()])
    }
  )
}

@Composable
private fun SharedWishlistsSettings.icon() = when (this) {
  SharedWishlistsSettings.BackToPrivate -> Icons.Outlined.SettingsBackupRestore
}

@Composable
private fun SharedWishlistsSettings.text() = when (this) {
  SharedWishlistsSettings.BackToPrivate -> R.string.shared_wishlists_wishlist_settings_back_to_privates
}.let { id -> stringResource(id) }

@Composable
private fun SharedWishlistsSettings.color() = when (this) {
  SharedWishlistsSettings.BackToPrivate -> null
}
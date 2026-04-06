package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistsSettingsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSettingClick: (WishlistsSettings) -> Unit,
) {

  SettingsBottomSheet(
    visible = visible,
    sheetState = sheetState,
    settings = WishlistsSettings.entries.map { setting ->
      SettingsBottomSheet.Option(
        id = setting.name,
        icon = setting.icon(),
        text = setting.text(),
      )
    },
    onDismiss = onDismiss,
    onSettingClick = { option ->
      val setting = WishlistsSettings.valueOf(option.id)
      onSettingClick(setting)
      onDismiss()
    }
  )
}

private fun WishlistsSettings.icon() = when (this) {
  WishlistsSettings.Search -> Icons.Rounded.Search
  WishlistsSettings.Filter -> Icons.Outlined.FilterAlt
  WishlistsSettings.AdminCategories -> Icons.Outlined.Sell
}

@Composable
private fun WishlistsSettings.text() = when (this) {
  WishlistsSettings.Search -> R.string.wishlists_search
  WishlistsSettings.Filter -> R.string.wishlists_filter
  WishlistsSettings.AdminCategories -> R.string.wishlists_admin_categories
}.let { id -> stringResource(id) }
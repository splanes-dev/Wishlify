package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.model.SharedWishlistsSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedWishlistsSettingsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSettingClick: (SharedWishlistsSettings) -> Unit,
) {
  SettingsBottomSheet(
    visible = visible,
    sheetState = sheetState,
    settings = SharedWishlistsSettings.entries.map { setting ->
      SettingsBottomSheet.Option(
        id = setting.name,
        icon = setting.icon(),
        text = setting.text()
      )
    },
    onDismiss = onDismiss,
    onSettingClick = { option -> onSettingClick(SharedWishlistsSettings.valueOf(option.id)) }
  )
}

@Composable
private fun SharedWishlistsSettings.icon() = when (this) {
  SharedWishlistsSettings.Search -> Icons.Rounded.Search
  SharedWishlistsSettings.Filter -> Icons.Outlined.FilterAlt
}

@Composable
private fun SharedWishlistsSettings.text() = when (this) {
  SharedWishlistsSettings.Search -> R.string.wishlists_search
  SharedWishlistsSettings.Filter -> R.string.wishlists_filter
}.let { id -> stringResource(id) }
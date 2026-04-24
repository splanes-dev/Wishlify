package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.model.SharedWishlistSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedWishlistThirdPartySettingsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  settings: List<SharedWishlistSettings>,
  onDismiss: () -> Unit,
  onSettingClick: (SharedWishlistSettings) -> Unit
) {

  SettingsBottomSheet(
    visible = visible,
    sheetState = sheetState,
    settings = settings.mapIndexed { index, setting ->
      SettingsBottomSheet.Option(
        id = index.toString(),
        icon = setting.icon(),
        text = setting.text(),
      )
    },
    onDismiss = onDismiss,
    onSettingClick = { option ->
      onSettingClick(settings[option.id.toInt()])
    }
  )
}

@Composable
private fun SharedWishlistSettings.icon() = when (this) {
  SharedWishlistSettings.Filter -> Icons.Outlined.FilterAlt
  SharedWishlistSettings.Info -> Icons.Outlined.Info
}

@Composable
private fun SharedWishlistSettings.text() = when (this) {
  SharedWishlistSettings.Filter -> R.string.filter_items
  SharedWishlistSettings.Info -> R.string.wishlists_info
}.let { id -> stringResource(id) }
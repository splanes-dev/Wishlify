package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own.model.SharedOwnWishlistSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedOwnWishlistDetailSettingsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  settings: List<SharedOwnWishlistSettings>,
  onDismiss: () -> Unit,
  onSettingClick: (SharedOwnWishlistSettings) -> Unit,
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
      val index = option.id.toInt()
      onSettingClick(settings[index])
    }
  )
}

@Composable
private fun SharedOwnWishlistSettings.icon() =
  when (this) {
    SharedOwnWishlistSettings.Filter -> Icons.Outlined.FilterAlt
    SharedOwnWishlistSettings.BackToPrivates -> Icons.Outlined.SettingsBackupRestore
  }

@Composable
private fun SharedOwnWishlistSettings.text() =
  when (this) {
    SharedOwnWishlistSettings.Filter -> R.string.filter_items
    SharedOwnWishlistSettings.BackToPrivates -> R.string.shared_wishlists_wishlist_settings_back_to_privates
  }.let { id -> stringResource(id) }


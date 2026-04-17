package com.splanes.uoc.wishlify.presentation.feature.groups.feature.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.model.GroupSettings
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSettingsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSettingClick: (GroupSettings) -> Unit,
) {
  SettingsBottomSheet(
    visible = visible,
    sheetState = sheetState,
    settings = GroupSettings.entries.map { setting ->
      SettingsBottomSheet.Option(
        id = setting.name,
        icon = setting.icon(),
        text = setting.text(),
        contentColor = WishlifyTheme.colorScheme.error.takeIf { setting == GroupSettings.LeaveGroup }
      )
    },
    onDismiss = onDismiss,
    onSettingClick = { option ->
      val setting = GroupSettings.valueOf(option.id)
      onSettingClick(setting)
    }
  )
}

private fun GroupSettings.icon() = when (this) {
  GroupSettings.Edit -> Icons.Outlined.BorderColor
  GroupSettings.LeaveGroup -> Icons.AutoMirrored.Outlined.ExitToApp
}

@Composable
private fun GroupSettings.text() = when (this) {
  GroupSettings.Edit -> R.string.groups_edit_group
  GroupSettings.LeaveGroup -> R.string.groups_leave_group
}.let { id -> stringResource(id) }
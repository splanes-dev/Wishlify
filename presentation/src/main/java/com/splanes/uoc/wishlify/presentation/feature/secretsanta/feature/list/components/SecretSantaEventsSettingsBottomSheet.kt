package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.model.SecretSantaEventsSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretSantaEventsSettingsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSettingClick: (SecretSantaEventsSettings) -> Unit,
) {
  SettingsBottomSheet(
    visible = visible,
    sheetState = sheetState,
    settings = SecretSantaEventsSettings.entries.map { setting ->
      SettingsBottomSheet.Option(
        id = setting.name,
        icon = setting.icon(),
        text = setting.text()
      )
    },
    onDismiss = onDismiss,
    onSettingClick = { option -> onSettingClick(SecretSantaEventsSettings.valueOf(option.id)) }
  )
}

@Composable
private fun SecretSantaEventsSettings.icon() = when (this) {
  SecretSantaEventsSettings.Search -> Icons.Rounded.Search
}

@Composable
private fun SecretSantaEventsSettings.text() = when (this) {
  SecretSantaEventsSettings.Search -> R.string.secret_santa_search
}.let { id -> stringResource(id) }
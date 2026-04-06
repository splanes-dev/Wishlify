package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.ToggleButtonShape

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> TabSelector(
  selected: T,
  tabs: List<T>,
  modifier: Modifier = Modifier,
  tabText: @Composable (tab: T) -> String,
  onClick: (T) -> Unit
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {

    Spacer(Modifier.width(32.dp))

    tabs.forEach { tab ->
      ToggleButton(
        modifier = Modifier.weight(1f),
        shapes = ToggleButtonShape,
        checked = selected == tab,
        onCheckedChange = { onClick(tab) }
      ) {
        ButtonText(text = tabText(tab))
      }
    }

    Spacer(Modifier.width(32.dp))
  }
}
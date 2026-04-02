package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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

  val density = LocalDensity.current
  var maxWidth by remember { mutableStateOf(0.dp) }

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    tabs.forEach { tab ->
      ToggleButton(
        modifier = Modifier
          .widthIn(min = maxWidth)
          .onSizeChanged { size ->
            val dp = with(density) { size.width.toDp() }
            if (dp > maxWidth) {
              maxWidth = dp
            }
          },
        shapes = ToggleButtonShape,
        checked = selected == tab,
        onCheckedChange = { onClick(tab) }
      ) {
        ButtonText(text = tabText(tab))
      }
    }
  }
}
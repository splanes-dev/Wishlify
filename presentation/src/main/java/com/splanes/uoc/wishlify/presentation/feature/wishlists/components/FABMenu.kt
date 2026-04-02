package com.splanes.uoc.wishlify.presentation.feature.wishlists.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FABMenu(
  modifier: Modifier = Modifier,
  content: @Composable FloatingActionButtonMenuScope.(collapse: () -> Unit) -> Unit
) {

  var isChecked by remember { mutableStateOf(false) }

  FloatingActionButtonMenu(
    modifier = modifier,
    expanded = isChecked,
    button = {
      ToggleFloatingActionButton(
        checked = isChecked,
        onCheckedChange = { isChecked = !isChecked },
        containerColor = ToggleFloatingActionButtonDefaults.containerColor(
          initialColor = WishlifyTheme.colorScheme.tertiaryContainer,
          finalColor = WishlifyTheme.colorScheme.tertiary
        ),
        containerCornerRadius = ToggleFloatingActionButtonDefaults.containerCornerRadius(
          initialSize = 12.dp
        )
      ) {
        Icon(
          imageVector = if (isChecked) {
            Icons.Rounded.Close
          } else {
            Icons.Rounded.Add
          },
          contentDescription = null,
          tint = if (isChecked) {
            WishlifyTheme.colorScheme.onTertiary
          } else {
            WishlifyTheme.colorScheme.onTertiaryContainer
          }
        )
      }
    },
    content = { this.content { isChecked = false } }
  )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FloatingActionButtonMenuScope.FABMenuItem(
  icon: Painter,
  text: String,
  onClick: () -> Unit,
) {
  FloatingActionButtonMenuItem(
    icon = { Icon(painter = icon, contentDescription = text) },
    text = { Text(text = text) },
    containerColor = WishlifyTheme.colorScheme.tertiaryContainer,
    onClick = onClick
  )
}
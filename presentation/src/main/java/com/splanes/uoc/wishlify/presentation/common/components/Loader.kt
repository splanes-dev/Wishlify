package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Loader(
  modifier: Modifier = Modifier,
  containerColor: Color = WishlifyTheme.colorScheme.onSurface.copy(
    alpha = .3f
  ),
  contentColor: Color = WishlifyTheme.colorScheme.primary,
  text: String? = null
) {
  Box(
    modifier = modifier
      .background(containerColor)
      .clickable(
        interactionSource = null,
        indication = null,
        onClick = { /* Nothing, just to block screen clicks */ }
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      LoadingIndicator(color = contentColor)
      text?.let {
        Text(
          text = text,
          style = WishlifyTheme.typography.titleMedium,
          color = WishlifyTheme.colorScheme.onSurface
        )
      }
    }
  }
}
package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun EmptyState(
  image: Painter,
  title: String,
  description: String,
  modifier: Modifier = Modifier,
  button: (@Composable () -> Unit)? = null
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      style = WishlifyTheme.typography.headlineMedium,
      color = WishlifyTheme.colorScheme.onSurface,
    )

    Image(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      painter = image,
      contentDescription = description
    )

    Text(
      text = description,
      textAlign = TextAlign.Center,
      style = WishlifyTheme.typography.titleLarge,
      color = WishlifyTheme.colorScheme.onSurface,
    )

    button?.invoke()

  }
}
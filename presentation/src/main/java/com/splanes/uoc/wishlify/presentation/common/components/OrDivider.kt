package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun OrDivider(
  text: String,
  modifier: Modifier = Modifier,
  dividerWidth: Dp = 24.dp,
  dividerColor: Color = WishlifyTheme.colorScheme.outline
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    HorizontalDivider(
      modifier = Modifier.width(dividerWidth),
      color = dividerColor
    )

    Text(
      modifier = Modifier.padding(horizontal = 8.dp),
      text = text,
      style = WishlifyTheme.typography.titleMedium,
      color = dividerColor
    )

    HorizontalDivider(
      modifier = Modifier.width(dividerWidth),
      color = dividerColor
    )
  }
}
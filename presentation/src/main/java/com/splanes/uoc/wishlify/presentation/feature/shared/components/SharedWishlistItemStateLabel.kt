package com.splanes.uoc.wishlify.presentation.feature.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.containerColor
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.contentColor
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.icon
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.name
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SharedWishlistItemStateLabel(
  state: SharedWishlistItem.State,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.extraSmall,
    color = state.containerColor()
  ) {
    Row(
      modifier = Modifier.padding(
        vertical = 4.dp,
        horizontal = 8.dp
      ),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        modifier = Modifier.size(20.dp),
        painter = state.icon(),
        contentDescription = state.name(),
        tint = state.contentColor(),
      )

      Text(
        text = state.name(),
        style = WishlifyTheme.typography.labelSmall,
        color = state.contentColor(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
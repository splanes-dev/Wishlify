package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SharedWishlistItemInfoBanner(
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.secondaryContainer
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          start = 8.dp,
          top = 8.dp,
          bottom = 8.dp
        ),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Outlined.Info,
        contentDescription = null,
        tint = WishlifyTheme.colorScheme.onSecondaryContainer
      )

      Text(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 16.dp),
        text = stringResource(R.string.shared_wishlists_items_collaborate_info_banner),
        style = WishlifyTheme.typography.bodySmall,
        color = WishlifyTheme.colorScheme.onSecondaryContainer
      )

      Surface(
        color = Color.Transparent,
        shape = WishlifyTheme.shapes.extraSmall,
        onClick = onDismiss
      ) {
        Icon(
          modifier = Modifier.padding(4.dp),
          imageVector = Icons.Rounded.Close,
          contentDescription = stringResource(R.string.close),
          tint =WishlifyTheme.colorScheme.onSecondaryContainer
        )
      }
    }
  }
}

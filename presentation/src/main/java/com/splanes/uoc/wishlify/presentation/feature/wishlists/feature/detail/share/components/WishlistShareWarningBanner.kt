package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun WishlistShareWarningBanner(
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.medium,
    color = WishlifyTheme.colorScheme.warningContainer
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          modifier = Modifier.size(20.dp),
          imageVector = Icons.Rounded.WarningAmber,
          contentDescription = stringResource(R.string.wishlists_share_banner_info_title),
          tint = WishlifyTheme.colorScheme.onWarningContainer
        )

        Text(
          text = stringResource(R.string.wishlists_share_banner_info_title),
          style = WishlifyTheme.typography.titleSmall,
          color = WishlifyTheme.colorScheme.onWarningContainer,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        modifier = Modifier.padding(start = 28.dp),
        text = AnnotatedString.fromHtml(stringResource(R.string.wishlists_share_banner_info_description)),
        textAlign = TextAlign.Justify,
        style = WishlifyTheme.typography.bodySmall,
        color = WishlifyTheme.colorScheme.onWarningContainer,
      )

      Spacer(Modifier.height(4.dp))
    }
  }
}
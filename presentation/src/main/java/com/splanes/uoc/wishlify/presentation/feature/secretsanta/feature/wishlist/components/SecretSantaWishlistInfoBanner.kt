package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.wishlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SecretSantaWishlistInfoBanner(
  isOwnWishlist: Boolean,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.infoContainer
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Outlined.Visibility,
          contentDescription = null,
          tint = WishlifyTheme.colorScheme.onInfoContainer
        )

        Text(
          text = stringResource(R.string.secret_santa_wishlist_info_banner_title),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onInfoContainer,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        modifier = Modifier.padding(start = 32.dp),
        text = if (isOwnWishlist) {
          R.string.secret_santa_wishlist_info_banner_description_own
        } else {
          R.string.secret_santa_wishlist_info_banner_description
        }.let { id -> htmlString(id) },
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onInfoContainer,
        textAlign = TextAlign.Justify
      )
    }
  }
}
package com.splanes.uoc.wishlify.presentation.feature.groups.feature.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SearchUsersInfoBanner(
  modifier: Modifier = Modifier,
  onClose: () -> Unit
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.infoContainer
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Outlined.Info,
          contentDescription = stringResource(R.string.close),
          tint = WishlifyTheme.colorScheme.onInfoContainer
        )

        Spacer(Modifier.width(8.dp))

        Text(
          text = stringResource(R.string.groups_search_users_info_banner_title),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onInfoContainer,
          fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.weight(1f))

        Surface(
          color = Color.Transparent,
          shape = WishlifyTheme.shapes.extraSmall,
          onClick = onClose,
        ) {
          Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = stringResource(R.string.close),
            tint = WishlifyTheme.colorScheme.onInfoContainer
          )
        }
      }

      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(
            start = 40.dp,
            end = 32.dp
          ),
        text = stringResource(R.string.groups_search_users_info_banner_description),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onInfoContainer,
        textAlign = TextAlign.Justify
      )

      Spacer(Modifier.height(8.dp))
    }
  }
}
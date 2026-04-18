package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
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
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun ProfileUpdateEmailBanner(
  modifier: Modifier,
  description: String,
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.warningContainer
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
          imageVector = Icons.Outlined.WarningAmber,
          contentDescription = null,
          tint = WishlifyTheme.colorScheme.onWarningContainer
        )

        Text(
          text = stringResource(R.string.attention),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onWarningContainer,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        modifier = Modifier.padding(start = 32.dp),
        text = description,
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onWarningContainer,
        textAlign = TextAlign.Justify
      )
    }
  }
}
package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonCustom
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SecretSantaChatBanner(
  modifier: Modifier = Modifier,
  onClose: () -> Unit,
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
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Rounded.WarningAmber,
          contentDescription = null,
          tint = WishlifyTheme.colorScheme.onWarningContainer
        )

        Text(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp),
          text = stringResource(R.string.secret_santa_event_detail_draw_pending_banner_title),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onWarningContainer,
          fontWeight = FontWeight.Bold
        )

        IconButtonCustom(
          imageVector = Icons.Rounded.Close,
          contentSize = DpSize(24.dp, 24.dp),
          onClick = onClose
        )
      }

      Text(
        modifier = Modifier.padding(
          start = 32.dp,
          end = 16.dp
        ),
        text = htmlString(R.string.secret_santa_anonymous_chat_banner),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onWarningContainer,
        textAlign = TextAlign.Justify
      )
    }
  }
}
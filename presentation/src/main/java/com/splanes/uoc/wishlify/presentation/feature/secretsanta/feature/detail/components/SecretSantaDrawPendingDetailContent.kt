package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.utils.isExpired
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.model.SecretSantaDetailAction
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaDrawPendingDetailContent(
  event: SecretSantaEventDetail.DrawPending,
  modifier: Modifier = Modifier,
  onAction: (SecretSantaDetailAction.DrawPending) -> Unit
) {

  val expired = event.deadline.isExpired()

  Column(
    modifier = modifier,
    verticalArrangement = if (expired) {
      Arrangement.Center
    } else {
      Arrangement.Top
    }
  ) {
    EmptyState(
      image = painterResource(R.drawable.img_secret_santa_draw_pending),
      title = stringResource(R.string.secret_santa_event_detail_draw_pending_title),
      description = if (expired) {
        R.string.secret_santa_event_detail_draw_pending_expired_description
      } else {
        R.string.secret_santa_event_detail_draw_pending_description
      }.let { id -> stringResource(id) }
    )

    if (!expired) {
      WarningBanner(modifier = Modifier.padding(top = 24.dp))

      Spacer(Modifier.weight(1f))

      OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        shapes = ButtonShape,
        border = BorderStroke(width = 1.dp, color = WishlifyTheme.colorScheme.onWarningContainer),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = WishlifyTheme.colorScheme.onWarningContainer
        ),
        onClick = { onAction(SecretSantaDetailAction.EditEvent) }
      ) {
        Icon(
          imageVector = Icons.Outlined.BorderColor,
          contentDescription = stringResource(R.string.secret_santa_event_edit_conditions)
        )

        Spacer(Modifier.width(8.dp))

        ButtonText(text = stringResource(R.string.secret_santa_event_edit_conditions))
      }

      Spacer(Modifier.height(16.dp))

      Button(
        modifier = Modifier.fillMaxWidth(),
        shapes = ButtonShape,
        onClick = { onAction(SecretSantaDetailAction.DoDraw) }
      ) {
        ButtonText(text = stringResource(R.string.secret_santa_event_do_draw))
      }
    }
  }
}

@Composable
private fun WarningBanner(modifier: Modifier) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.warningContainer
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          start = 8.dp,
          top = 8.dp,
          bottom = 8.dp
        ),
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
            .padding(horizontal = 16.dp),
          text = stringResource(R.string.secret_santa_event_detail_draw_pending_banner_title),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onWarningContainer,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        modifier = Modifier.padding(
          start = 40.dp,
          end = 16.dp
        ),
        text = stringResource(R.string.secret_santa_event_detail_draw_pending_banner_description),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onWarningContainer
      )
    }
  }
}
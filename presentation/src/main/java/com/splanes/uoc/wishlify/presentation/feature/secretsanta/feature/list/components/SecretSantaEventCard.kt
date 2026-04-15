package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.CardImage
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SecretSantaEventCard(
  event: SecretSantaEvent,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Card(
    modifier = modifier
      .height(120.dp)
      .alpha(if (event.isFinished()) .7f else 1f),
    shape = WishlifyTheme.shapes.small,
    border = BorderStroke(
      width = 1.dp,
      color = WishlifyTheme.colorScheme.outline.copy(alpha = .08f)
    ),
    elevation = CardDefaults.elevatedCardElevation(
      defaultElevation = 1.dp
    ),
    onClick = onClick
  ) {
    Row(modifier = Modifier.fillMaxWidth()) {
      CardImage(
        media = event.photoUrl?.let(ImageMedia::Url),
        enabled = !event.isFinished(),
        placeholder = painterResource(R.drawable.img_secret_santa_event_placeholder)
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = event.name,
          style = WishlifyTheme.typography.titleMedium,
          color = WishlifyTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        DrawState(event)

        Spacer(Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Spacer(Modifier.weight(1f))
          DateInfo(event)
        }
      }
    }
  }
}

@Composable
private fun DrawState(event: SecretSantaEvent) {
  when (event) {
    is SecretSantaEvent.DrawDone -> {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          modifier = Modifier.size(20.dp),
          painter = painterResource(R.drawable.ic_secret_santa),
          contentDescription = event.target,
          tint = WishlifyTheme.colorScheme.secondary
        )

        Text(
          text = event.target,
          style = WishlifyTheme.typography.bodyLarge,
          color = WishlifyTheme.colorScheme.secondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    is SecretSantaEvent.DrawPending -> {
      Surface(
        shape = WishlifyTheme.shapes.extraSmall,
        color = WishlifyTheme.colorScheme.warningContainer
      ) {
        Text(
          modifier = Modifier.padding(
            horizontal = 4.dp,
            vertical = 2.dp
          ),
          text = stringResource(R.string.secret_santa_draw_pending),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.warning
        )
      }
    }
  }
}

@Composable
private fun DateInfo(event: SecretSantaEvent) {
  if (event.isFinished()) {
    Surface(
      shape = WishlifyTheme.shapes.extraSmall,
      color = Color.Transparent,
      border = BorderStroke(width = 1.dp, color = WishlifyTheme.colorScheme.outline)
    ) {
      Text(
        modifier = Modifier.padding(4.dp),
        text = stringResource(R.string.event_finished),
        style = WishlifyTheme.typography.labelSmall,
        color = WishlifyTheme.colorScheme.onSurface
      )
    }
  } else {
    Text(
      text = event.deadline.formatted(),
      style = WishlifyTheme.typography.labelSmall,
      color = WishlifyTheme.colorScheme.outline,
    )

    Spacer(modifier = Modifier.width(4.dp))

    Icon(
      modifier = Modifier.size(16.dp),
      imageVector = Icons.Filled.Event,
      contentDescription = event.deadline.formatted(),
      tint = WishlifyTheme.colorScheme.outline
    )
  }
}
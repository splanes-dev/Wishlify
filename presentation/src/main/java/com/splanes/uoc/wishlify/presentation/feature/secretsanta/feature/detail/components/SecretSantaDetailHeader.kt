package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.common.utils.isExpired
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.formatPrice
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import java.util.Date

@Composable
fun SecretSantaDetailHeader(
  detail: SecretSantaEventDetail,
  modifier: Modifier = Modifier
) {

  val group = detail.group

  Column(
    modifier = modifier.background(color = WishlifyTheme.colorScheme.surface),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        modifier = Modifier.size(24.dp),
        painter = painterResource(R.drawable.ic_money_bag),
        contentDescription = null,
        tint = WishlifyTheme.colorScheme.secondary
      )

      Spacer(Modifier.width(8.dp))

      Text(
        text = detail.budget.toFloat().formatPrice(),
        style = WishlifyTheme.typography.titleMedium,
        color = WishlifyTheme.colorScheme.secondary
      )

      if (detail.isBudgetApproximate) {
        Spacer(Modifier.width(2.dp))

        Text(
          text = stringResource(R.string.secret_santa_event_detail_header_budget_approx),
          style = WishlifyTheme.typography.bodySmall,
          color = WishlifyTheme.colorScheme.secondary
        )
      }

      Spacer(Modifier.weight(1f))

      Deadline(detail.deadline)
    }

    HeaderInfo(
      icon = rememberVectorPainter(Icons.Outlined.Group),
      text = when {
        group != null && detail.participants.count() != 0 ->
          stringResource(
            R.string.shared_wishlists_detail_participants_header,
            group.membersCount + detail.participants.count()
          )

        group != null -> group.name
        else -> stringResource(
          R.string.shared_wishlists_detail_participants_header,
          detail.participants.count()
        )
      }
    )

    if (detail.deadline.isExpired()) {
      InfoBanner(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
      )
    }

    HorizontalDivider(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
      color = WishlifyTheme.colorScheme.secondary
    )
  }
}

@Composable
private fun HeaderInfo(
  icon: Painter,
  text: String
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Icon(
      modifier = Modifier.size(24.dp),
      painter = icon,
      contentDescription = text,
      tint = WishlifyTheme.colorScheme.secondary
    )

    Text(
      text = text,
      style = WishlifyTheme.typography.titleMedium,
      color = WishlifyTheme.colorScheme.secondary
    )
  }
}


@Composable
private fun Deadline(deadline: Date) {

  val expired = deadline.isExpired()

  val containerColor = if (expired) {
    WishlifyTheme.colorScheme.error.copy(alpha = .7f)
  } else {
    WishlifyTheme.colorScheme.tertiaryContainer
  }

  val contentColor = if (expired) {
    WishlifyTheme.colorScheme.onError
  } else {
    WishlifyTheme.colorScheme.onTertiaryContainer
  }

  val icon = if (expired) {
    Icons.Rounded.EventBusy
  } else {
    Icons.Rounded.Event
  }

  val text = if (expired) {
    stringResource(R.string.event_finished)
  } else {
    deadline.time.formatted()
  }

  Surface(
    color = containerColor,
    shape = WishlifyTheme.shapes.extraSmall
  ) {
    Row(
      modifier = Modifier
        .padding(
          vertical = 4.dp,
          horizontal = 8.dp
        ),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        modifier = Modifier.size(24.dp),
        imageVector = icon,
        contentDescription = stringResource(R.string.deadline),
        tint = contentColor
      )

      Text(
        text = text,
        style = WishlifyTheme.typography.titleMedium,
        color = contentColor
      )
    }
  }
}

@Composable
private fun InfoBanner(modifier: Modifier = Modifier) {
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
        text = htmlString(R.string.secret_santa_event_detail_header_expired_info_banner),
        style = WishlifyTheme.typography.bodySmall,
        color = WishlifyTheme.colorScheme.onSecondaryContainer
      )
    }
  }
}
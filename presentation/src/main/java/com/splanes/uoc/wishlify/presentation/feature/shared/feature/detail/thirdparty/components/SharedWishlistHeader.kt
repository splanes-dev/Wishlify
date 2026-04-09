package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.rounded.Event
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import java.util.Date

@Composable
fun SharedWishlistHeader(
  group: Group.Basic?,
  participantsCount: Int,
  itemsAvailableCount: Int,
  deadline: Date,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.background(color = WishlifyTheme.colorScheme.surface),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      HeaderInfo(
        icon = painterResource(R.drawable.ic_gift),
        text = pluralStringResource(
          R.plurals.shared_wishlists_available_items_count,
          itemsAvailableCount,
          itemsAvailableCount
        )
      )

      Spacer(Modifier.weight(1f))

      Deadline(deadline)
    }

    HeaderInfo(
      icon = rememberVectorPainter(Icons.Outlined.Group),
      text = when {
        group != null && participantsCount != 0 ->
          stringResource(
            R.string.shared_wishlists_detail_participants_header,
            group.membersCount + participantsCount
          )

        group != null -> group.name
        else -> stringResource(
          R.string.shared_wishlists_detail_participants_header,
          participantsCount
        )
      }
    )

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
fun Deadline(deadline: Date) {
  Surface(
    color = WishlifyTheme.colorScheme.tertiaryFixed,
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
        imageVector = Icons.Rounded.Event,
        contentDescription = stringResource(R.string.deadline),
        tint = WishlifyTheme.colorScheme.onTertiaryContainer
      )

      Text(
        text = deadline.time.formatted(),
        style = WishlifyTheme.typography.titleMedium,
        color = WishlifyTheme.colorScheme.onTertiaryContainer
      )
    }
  }
}
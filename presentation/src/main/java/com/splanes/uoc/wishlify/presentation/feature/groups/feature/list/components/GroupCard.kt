package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.image.RemoteImage
import com.splanes.uoc.wishlify.presentation.feature.groups.components.GroupStateLabel
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun GroupCard(
  group: Group.Basic,
  modifier: Modifier = Modifier,
  onSettingsClick: () -> Unit,
  onClick: () -> Unit,
) {
  Card(
    modifier = modifier.height(120.dp),
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
      when (val image = group.photoUrl) {
        null -> {
          Image(
            modifier = Modifier
              .width(135.dp)
              .fillMaxHeight()
              .background(color = WishlifyTheme.colorScheme.surfaceBright),
            painter = painterResource(R.drawable.preset_group),
            contentDescription = group.name,
            contentScale = ContentScale.Crop
          )
        }

        else -> {
          RemoteImage(
            modifier = Modifier
              .width(135.dp)
              .fillMaxHeight(),
            url = image,
            contentScale = ContentScale.Crop
          )
        }
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            modifier = Modifier.weight(1f),
            text = group.name,
            style = WishlifyTheme.typography.titleMedium,
            color = WishlifyTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          if (group.isInactive) {
            Icon(
              modifier = Modifier
                .size(16.dp)
                .clickable { onSettingsClick() },
              painter = painterResource(R.drawable.ic_item_settings),
              tint = WishlifyTheme.colorScheme.onSurface,
              contentDescription = null
            )
          }
        }

        Spacer(Modifier.height(8.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Group,
            contentDescription = "",
            tint = WishlifyTheme.colorScheme.secondary,
          )

          Text(
            text = stringResource(R.string.groups_list_member_count, group.membersCount),
            style = WishlifyTheme.typography.bodySmall,
            color = WishlifyTheme.colorScheme.secondary,
            maxLines = 1,
          )
        }

        Spacer(Modifier.weight(1f))

        Row(
          modifier = Modifier,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Spacer(Modifier.weight(1f))

          GroupStateLabel(state = group.state)
        }
      }
    }
  }
}
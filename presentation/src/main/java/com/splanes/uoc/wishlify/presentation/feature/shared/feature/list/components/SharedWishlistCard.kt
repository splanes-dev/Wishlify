package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.CardImage
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonCustom
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SharedWishlistCard(
  sharedWishlist: SharedWishlist,
  modifier: Modifier = Modifier,
  onSettingsClick: () -> Unit,
  onClick: () -> Unit,
) {
  Card(
    modifier = modifier
      .height(120.dp)
      .alpha(if (sharedWishlist.isFinished()) .7f else 1f),
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
        media = sharedWishlist.linkedWishlist.photo,
        enabled = !sharedWishlist.isFinished()
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            modifier = Modifier.weight(1f),
            text = sharedWishlist.linkedWishlist.name,
            style = WishlifyTheme.typography.titleMedium,
            color = WishlifyTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          when (sharedWishlist) {
            is SharedWishlist.Own if sharedWishlist.isFinished() -> {
              IconButtonCustom(
                painter = painterResource(R.drawable.ic_item_settings),
                contentColor = WishlifyTheme.colorScheme.onSurface,
                onClick = onSettingsClick
              )
            }

            is SharedWishlist.ThirdParty if sharedWishlist.pendingNotificationsCount != 0 -> {
              val count = sharedWishlist.pendingNotificationsCount
              Box(
                modifier = Modifier
                  .background(
                    color = WishlifyTheme.colorScheme.error,
                    shape = CircleShape
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  modifier = Modifier.padding(4.dp),
                  text = count.toString(),
                  style = WishlifyTheme.typography.labelLarge,
                  color = WishlifyTheme.colorScheme.onError
                )
              }
            }

            else -> {
              // Nothing to do
            }
          }
        }

        TargetText(
          modifier = Modifier.padding(top = 4.dp),
          sharedWishlist = sharedWishlist
        )

        Spacer(Modifier.height(8.dp))

        when (sharedWishlist) {
          is SharedWishlist.Own -> {
            CardInfo(
              icon = painterResource(R.drawable.ic_gift),
              text = pluralStringResource(
                R.plurals.wishlists_list_item_count,
                count = sharedWishlist.numOfItems,
                sharedWishlist.numOfItems
              ),
            )
          }
          is SharedWishlist.ThirdParty -> {
            ParticipantsInfo(sharedWishlist)
          }
        }

        Spacer(Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Spacer(Modifier.weight(1f))
          DateInfo(sharedWishlist)
        }
      }
    }
  }
}

@Composable
private fun TargetText(
  modifier: Modifier,
  sharedWishlist: SharedWishlist
) {

  val text = when {
    sharedWishlist is SharedWishlist.ThirdParty -> {
      sharedWishlist.target
    }
    sharedWishlist.linkedWishlist.target != null -> {
      sharedWishlist.linkedWishlist.target
    }
    else -> null
  }

  text?.let { target ->
    Text(
      modifier = modifier,
      text = target,
      style = WishlifyTheme.typography.bodyMedium,
      color = WishlifyTheme.colorScheme.onSurfaceVariant,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
private fun ParticipantsInfo(
  sharedWishlist: SharedWishlist.ThirdParty
) {

  val group = sharedWishlist.group
  val participants = sharedWishlist.participants

  val text = when {
    group != null && participants.isNotEmpty() -> {
      val count = group.membersCount + participants.count()
      pluralStringResource(
        R.plurals.shared_wishlists_list_participants_card,
        count,
        count
      )
    }
    group != null -> {
      group.name
    }
    else -> {
      pluralStringResource(
        R.plurals.shared_wishlists_list_participants_card,
        participants.count(),
        participants.count()
      )
    }
  }

  CardInfo(
    icon = rememberVectorPainter(Icons.Outlined.Group),
    text = text,
  )
}

@Composable
private fun DateInfo(
  sharedWishlist: SharedWishlist
) {
  if (sharedWishlist.isFinished()) {
    Surface(
      shape = WishlifyTheme.shapes.extraSmall,
      border = BorderStroke(width = 1.dp, color = WishlifyTheme.colorScheme.outline)
    ) {
      Text(
        modifier = Modifier.padding(4.dp),
        text = stringResource(R.string.finished),
        style = WishlifyTheme.typography.labelSmall,
        color = WishlifyTheme.colorScheme.onSurface
      )
    }
  } else {
    Text(
      text = sharedWishlist.deadline.formatted(),
      style = WishlifyTheme.typography.labelSmall,
      color = WishlifyTheme.colorScheme.outline,
    )

    Spacer(modifier = Modifier.width(4.dp))

    Icon(
      modifier = Modifier.size(16.dp),
      imageVector = Icons.Filled.Event,
      contentDescription = sharedWishlist.deadline.formatted(),
      tint = WishlifyTheme.colorScheme.outline
    )
  }
}

@Composable
private fun CardInfo(
  icon: Painter,
  text: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Icon(
      modifier = Modifier.size(16.dp),
      painter = icon,
      contentDescription = null,
      tint = WishlifyTheme.colorScheme.secondary,
    )

    Text(
      text = text,
      style = WishlifyTheme.typography.bodySmall,
      color = WishlifyTheme.colorScheme.secondary
    )
  }
}
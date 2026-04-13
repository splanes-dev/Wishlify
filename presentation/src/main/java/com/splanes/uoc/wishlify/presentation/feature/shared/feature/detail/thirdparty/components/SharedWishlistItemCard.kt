package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.CardImage
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonCustom
import com.splanes.uoc.wishlify.presentation.feature.shared.components.SharedWishlistItemStateLabel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.color
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.formatPrice
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.icon
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.name
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SharedWishlistItemCardAnimated(
  item: SharedWishlistItem,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  onSettingsClick: () -> Unit,
) {
  val borderColor = item.state.borderColor()
  val infiniteTransition = rememberInfiniteTransition(label = "state_card")

  val animatedAlpha by infiniteTransition.animateFloat(
    initialValue = 0.45f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1400, easing = EaseInOut),
      repeatMode = RepeatMode.Reverse
    ),
    label = "border_alpha"
  )

  val animatedStroke by infiniteTransition.animateFloat(
    initialValue = 1.5f,
    targetValue = 2.7f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1400, easing = EaseInOut),
      repeatMode = RepeatMode.Reverse
    ),
    label = "border_width"
  )

  Box(
    modifier = modifier
      .then(
        if (item.state.isCurrentUserParticipant) {
          Modifier.drawBehind {
            val strokeWidth = animatedStroke.dp.toPx()
            val radius = 8.dp.toPx()

            drawRoundRect(
              brush = Brush.linearGradient(
                colors = listOf(
                  borderColor.copy(alpha = animatedAlpha),
                  borderColor.copy(alpha = animatedAlpha * 0.55f),
                  borderColor.copy(alpha = animatedAlpha),
                )
              ),
              cornerRadius = CornerRadius(radius, radius),
              style = Stroke(width = strokeWidth)
            )
          }
        } else {
          Modifier.border(
            width = 1.dp,
            color = borderColor,
            shape = WishlifyTheme.shapes.small
          )
        }
      )
  ) {
    SharedWishlistItemCard(
      item = item,
      onClick = onClick,
      onSettingsClick = onSettingsClick,
    )
  }
}

@Composable
fun SharedWishlistItemCard(
  item: SharedWishlistItem,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  onSettingsClick: () -> Unit,
) {

  Card(
    modifier = modifier.height(120.dp),
    shape = WishlifyTheme.shapes.small,
    elevation = CardDefaults.elevatedCardElevation(
      defaultElevation = 1.dp
    ),
    onClick = onClick
  ) {
    Row(modifier = Modifier.fillMaxWidth()) {
      CardImage(
        media = item.linkedItem.photoUrl?.let(ImageMedia::Url),
        placeholder = painterResource(R.drawable.item_placeholder),
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            modifier = Modifier.weight(1f),
            text = item.linkedItem.name,
            style = WishlifyTheme.typography.titleMedium,
            color = WishlifyTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          if (item.state.hasSettings()) {
            IconButtonCustom(
              painter = painterResource(R.drawable.ic_item_settings),
              contentColor = WishlifyTheme.colorScheme.onSurface,
              onClick = onSettingsClick
            )
          }
        }

        Spacer(Modifier.height(4.dp))

        Text(
          text = item.linkedItem.price.formatPrice(),
          style = WishlifyTheme.typography.bodyLarge,
          color = WishlifyTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(8.dp))

        if (item.linkedItem.priority != WishlistItem.Priority.Standard) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              modifier = Modifier.size(16.dp),
              imageVector = item.linkedItem.priority.icon(),
              contentDescription = item.linkedItem.priority.name(),
              tint = item.linkedItem.priority.color(),
            )

            Text(
              text = item.linkedItem.priority.name(),
              style = WishlifyTheme.typography.bodySmall,
              color = item.linkedItem.priority.color()
            )
          }
        }

        Spacer(Modifier.weight(1f))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Spacer(Modifier.weight(1f))

          if (item.state !is SharedWishlistItem.Available) {
            SharedWishlistItemStateLabel(item.state)
          }
        }
      }
    }
  }
}

@Composable
private fun SharedWishlistItem.State.borderColor() = when (this) {
  SharedWishlistItem.Available -> WishlifyTheme.colorScheme.outline.copy(alpha = .08f)
  is SharedWishlistItem.Lock -> WishlifyTheme.colorScheme.warning
  is SharedWishlistItem.Purchased -> WishlifyTheme.colorScheme.success
  is SharedWishlistItem.ShareRequest -> WishlifyTheme.colorScheme.info
}

private fun SharedWishlistItem.State.hasSettings() =
  this is SharedWishlistItem.Available || this is SharedWishlistItem.ShareRequest
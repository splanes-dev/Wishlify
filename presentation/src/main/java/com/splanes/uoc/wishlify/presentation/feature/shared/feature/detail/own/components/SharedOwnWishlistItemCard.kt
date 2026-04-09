package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.ItemImage
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.color
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.formatPrice
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.icon
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.name
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SharedOwnWishlistItemCard(
  item: SharedWishlistItem,
  modifier: Modifier = Modifier,
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
      ItemImage(
        photoUrl = item.linkedItem.photoUrl,
        purchased = false
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
      ) {
        Text(
          text = item.linkedItem.name,
          style = WishlifyTheme.typography.titleMedium,
          color = WishlifyTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

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
          if (item.linkedItem.store.isNotBlank()) {
            Icon(
              modifier = Modifier.size(16.dp),
              imageVector = Icons.Outlined.Store,
              contentDescription = item.linkedItem.store,
              tint = WishlifyTheme.colorScheme.secondary,
            )

            Text(
              text = item.linkedItem.store,
              style = WishlifyTheme.typography.bodySmall,
              color = WishlifyTheme.colorScheme.secondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }
  }
}
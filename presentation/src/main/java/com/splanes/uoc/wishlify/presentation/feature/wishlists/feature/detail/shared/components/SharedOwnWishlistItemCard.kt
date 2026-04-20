package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.shared.components

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
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components.ItemImage
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.color
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.formatPrice
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.icon
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.name
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SharedOwnWishlistItemCard(
  item: WishlistItem,
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
        photoUrl = item.photoUrl,
        purchased = false
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
      ) {
        Text(
          text = item.name,
          style = WishlifyTheme.typography.titleMedium,
          color = WishlifyTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = item.price.formatPrice(),
          style = WishlifyTheme.typography.bodyLarge,
          color = WishlifyTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(8.dp))

        if (item.priority != WishlistItem.Priority.Standard) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              modifier = Modifier.size(16.dp),
              imageVector = item.priority.icon(),
              contentDescription = item.priority.name(),
              tint = item.priority.color(),
            )

            Text(
              text = item.priority.name(),
              style = WishlifyTheme.typography.bodySmall,
              color = item.priority.color()
            )
          }
        }

        Spacer(Modifier.weight(1f))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (item.store.isNotBlank()) {
            Icon(
              modifier = Modifier.size(16.dp),
              imageVector = Icons.Outlined.Store,
              contentDescription = item.store,
              tint = WishlifyTheme.colorScheme.secondary,
            )

            Text(
              text = item.store,
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
package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.shared.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EuroSymbol
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.image.ImageOrPlaceholder
import com.splanes.uoc.wishlify.presentation.common.utils.capitalize
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.color
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.formatPrice
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.icon
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.name
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedOwnWishlistItemDetailBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  item: WishlistItem,
  onDismiss: () -> Unit,
  onOpenLink: (link: String) -> Unit,
) {
  if (visible) {
    ModalBottomSheet(
      sheetState = sheetState,
      onDismissRequest = onDismiss
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ItemImage(
            modifier = Modifier
              .size(155.dp, 135.dp)
              .border(
                width = 1.dp,
                color = WishlifyTheme.colorScheme.outlineVariant,
                shape = WishlifyTheme.shapes.small
              ),
            url = item.photoUrl
          )

          Column {
            Text(
              text = item.name,
              style = WishlifyTheme.typography.titleLarge,
              color = WishlifyTheme.colorScheme.onSurface
            )

            HorizontalDivider(
              modifier = Modifier.fillMaxWidth(),
              color = WishlifyTheme.colorScheme.outlineVariant
            )

            Spacer(Modifier.height(8.dp))

            ItemPrice(item)

            Spacer(Modifier.height(4.dp))

            if (item.priority != WishlistItem.Priority.Standard) {
              ItemPriority(item.priority)
            }

            Spacer(Modifier.height(4.dp))

            ItemAmount(item.amount)

            if (item.store.isNotBlank()) {
              ItemStore(item.store)
            }
          }
        }

        if (item.link.isNotBlank()) {
          ItemLink(onClick = { onOpenLink(item.link) })
        } else {
          Spacer(Modifier.height(16.dp))
        }

        ItemDescription(item.description)

        Spacer(Modifier.height(16.dp))
      }
    }
  }
}



@Composable
private fun ItemImage(
  modifier: Modifier,
  url: String?
) {
  Box(modifier = modifier) {
    ImageOrPlaceholder(
      modifier = Modifier
        .fillMaxSize()
        .padding(4.dp)
        .clip(WishlifyTheme.shapes.small),
      url = url,
      placeholder = painterResource(R.drawable.item_placeholder),
    )
  }
}

@Composable
private fun ItemPrice(item: WishlistItem) {
  Row(verticalAlignment = Alignment.Bottom) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = item.price.formatPrice(includeCurrency = false),
        style = WishlifyTheme.typography.titleLarge,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Spacer(Modifier.width(2.dp))

      Icon(
        modifier = Modifier.size(20.dp),
        imageVector = Icons.Rounded.EuroSymbol,
        contentDescription = item.price.formatPrice(includeCurrency = false),
        tint = WishlifyTheme.colorScheme.onSurface
      )
    }

    if (item.amount > 1) {
      Spacer(Modifier.width(6.dp))

      Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = stringResource(
          R.string.wishlists_item_price_per_unit,
          item.unitPrice.formatPrice(includeCurrency = false)
        ),
        style = WishlifyTheme.typography.labelSmall,
        color = WishlifyTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun ItemPriority(priority: WishlistItem.Priority) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      text = stringResource(R.string.wishlists_item_priority_label),
      style = WishlifyTheme.typography.labelSmall,
      color = WishlifyTheme.colorScheme.onSurfaceVariant
    )

    Icon(
      modifier = Modifier.size(16.dp),
      imageVector = priority.icon(),
      contentDescription = priority.name(),
      tint = priority.color()
    )

    Text(
      text = priority.name(),
      style = WishlifyTheme.typography.bodyMedium,
      color = priority.color()
    )
  }
}

@Composable
private fun ItemAmount(amount: Int) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      text = stringResource(R.string.wishlists_item_quantity_label),
      style = WishlifyTheme.typography.labelSmall,
      color = WishlifyTheme.colorScheme.onSurfaceVariant
    )

    Text(
      text = stringResource(R.string.wishlists_item_quantity_amount, amount),
      style = WishlifyTheme.typography.bodyMedium,
      color = WishlifyTheme.colorScheme.onSurface
    )
  }
}

@Composable
private fun ItemStore(store: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      text = stringResource(R.string.wishlists_item_store_label),
      style = WishlifyTheme.typography.labelSmall,
      color = WishlifyTheme.colorScheme.onSurfaceVariant
    )

    Text(
      text = store.capitalize(),
      style = WishlifyTheme.typography.bodyMedium,
      color = WishlifyTheme.colorScheme.onSurface
    )
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ItemLink(onClick: () -> Unit) {
  TextButton(
    shapes = ButtonShape,
    onClick = onClick,
  ) {
    Icon(
      modifier = Modifier.size(20.dp),
      imageVector = Icons.Rounded.Link,
      contentDescription = stringResource(R.string.wishlists_item_link_to_product),
    )

    Spacer(Modifier.width(4.dp))

    ButtonText(
      text = stringResource(R.string.wishlists_item_link_to_product),
      style = WishlifyTheme.typography.labelLarge
    )
  }
}

@Composable
private fun ItemDescription(description: String) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.surfaceContainerHigh
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Text(
        text = stringResource(R.string.wishlists_item_description_or_notes),
        style = WishlifyTheme.typography.labelSmall,
        color = WishlifyTheme.colorScheme.onSurfaceVariant
      )

      Spacer(Modifier.height(4.dp))

      Text(
        modifier = Modifier.padding(start = 8.dp),
        text = description.takeUnless { it.isBlank() }
          ?: stringResource(R.string.wishlists_item_description_or_notes_empty),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onSurface.let { color ->
          if (description.isBlank()) {
            color.copy(alpha = .6f)
          } else {
            color
          }
        },
        textAlign = TextAlign.Justify
      )
    }
  }
}
package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components

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
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.CardImage
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonCustom
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun WishlistCard(
  wishlist: Wishlist,
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
      CardImage(media = wishlist.photo)

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            modifier = Modifier.weight(1f),
            text = wishlist.title,
            style = WishlifyTheme.typography.titleMedium,
            color = WishlifyTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          IconButtonCustom(
            painter = painterResource(R.drawable.ic_item_settings),
            contentColor = WishlifyTheme.colorScheme.onSurface,
            onClick = onSettingsClick
          )
        }

        (wishlist as? Wishlist.ThirdParty)?.let { w ->
          Spacer(Modifier.height(4.dp))

          Text(
            text = w.target,
            style = WishlifyTheme.typography.bodyMedium,
            color = WishlifyTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(Modifier.height(8.dp))

        CardInfo(
          icon = painterResource(R.drawable.ic_gift),
          text = pluralStringResource(
            R.plurals.wishlists_list_item_count,
            count = wishlist.numOfItems,
            wishlist.numOfItems
          ),
        )

        Spacer(Modifier.height(2.dp))

        CardInfo(
          icon = painterResource(R.drawable.ic_person_edit),
          text = pluralStringResource(
            R.plurals.wishlists_list_editors,
            count = wishlist.editors.count(),
            wishlist.editors.count()
          ),
        )

        Spacer(Modifier.weight(1f))

        Row {
          Spacer(Modifier.weight(1f))
          wishlist.category?.let { wishlistCategory ->
            Text(
              text = wishlistCategory.category.name,
              style = WishlifyTheme.typography.labelSmall,
              color = WishlifyTheme.colorScheme.outline,
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
              modifier = Modifier.size(16.dp),
              imageVector = Icons.Filled.Sell,
              contentDescription = wishlistCategory.category.name,
              tint = wishlistCategory.category.color.color()
            )
          }
        }
      }
    }
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

@Composable
private fun Category.CategoryColor.color() = when (this) {
  Category.CategoryColor.Purple -> Color(0xFF7C4DFF)
  Category.CategoryColor.Blue -> Color(0xFF448AFF)
  Category.CategoryColor.Yellow -> Color(0xFFFFC107)
  Category.CategoryColor.Green -> Color(0xFF4CAF50)
  Category.CategoryColor.Red -> Color(0xFFF44336)
  Category.CategoryColor.Pink -> Color(0xFFE91E63)
  Category.CategoryColor.Orange -> Color(0xFFFF9800)
}
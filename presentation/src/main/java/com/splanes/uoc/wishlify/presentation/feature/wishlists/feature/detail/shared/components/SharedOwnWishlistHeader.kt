package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.shared.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.common.utils.isExpired
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import java.util.Date

@Composable
fun SharedOwnWishlistHeader(
  event: Wishlist.ShareEvent,
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
        icon = rememberVectorPainter(Icons.Outlined.Group),
        text = stringResource(R.string.wishlist_shared_event)
      )

      Spacer(Modifier.weight(1f))

      Deadline(deadline)
    }

    InfoBanner(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      event = event,
      expired = deadline.isExpired()
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
    stringResource(R.string.finished)
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
private fun InfoBanner(
  event: Wishlist.ShareEvent,
  expired: Boolean,
  modifier: Modifier = Modifier,
) {
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
        text = when (event) {
          is Wishlist.SecretSantaEvent -> R.string.wishlist_shared_secret_santa_info_banner
          is Wishlist.SharedWishlistEvent if expired -> R.string.shared_wishlists_own_wishlist_items_expired_info_banner
          is Wishlist.SharedWishlistEvent ->  R.string.shared_wishlists_own_wishlist_items_locked_info_banner
        }.let { id -> htmlString(id) },
        style = WishlifyTheme.typography.bodySmall,
        color = WishlifyTheme.colorScheme.onSecondaryContainer
      )
    }
  }
}
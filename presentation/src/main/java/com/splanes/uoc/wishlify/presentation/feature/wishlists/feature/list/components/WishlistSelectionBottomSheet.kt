package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.image.ImagePreset
import com.splanes.uoc.wishlify.presentation.common.components.image.RemoteImage
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistNewItemByShare
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistSelectionBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  wishlists: List<Wishlist>,
  itemByShare: WishlistNewItemByShare,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  onSelect: (Wishlist) -> Unit,
) {

  var selected: Wishlist? by remember(wishlists) { mutableStateOf(null) }
  val isButtonEnabled by remember { derivedStateOf { selected != null } }

  if (visible) {
    ModalBottomSheet(
      modifier = modifier,
      sheetState = sheetState,
      onDismissRequest = onDismiss
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp),
      ) {
        Text(
          text = stringResource(R.string.wishlists_select_wishlist_to_new_item_shared_title),
          style = WishlifyTheme.typography.titleLarge,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = stringResource(R.string.wishlists_select_wishlist_to_new_item_shared_description),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        PreviewShared(itemByShare)

        Spacer(modifier = Modifier.height(16.dp))

        wishlists.forEach { wishlist ->
          SelectableWishlist(
            modifier = Modifier.padding(bottom = 4.dp),
            wishlist = wishlist,
            selected = wishlist.id == selected?.id,
            onClick = { selected = wishlist.takeUnless { it.id == selected?.id } }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = {
            selected?.run(onSelect)
            selected = null
          }
        ) {
          ButtonText(
            text = stringResource(R.string.select)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun PreviewShared(itemByShare: WishlistNewItemByShare) {
  Surface(
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.secondaryContainer,
  ) {
    when (itemByShare) {
      is WishlistNewItemByShare.Uri ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.wishlists_select_wishlist_to_new_item_shared_uri),
            style = WishlifyTheme.typography.bodyMedium,
            color = WishlifyTheme.colorScheme.onSurface,
          )

          RemoteImage(
            modifier = Modifier
              .size(100.dp)
              .clip(WishlifyTheme.shapes.small),
            url = itemByShare.uri,
            contentScale = ContentScale.Crop,
          )
        }

      is WishlistNewItemByShare.Url ->
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = stringResource(R.string.wishlists_select_wishlist_to_new_item_shared_url),
            style = WishlifyTheme.typography.bodyMedium,
            color = WishlifyTheme.colorScheme.onSurface,
          )

          Text(
            text = itemByShare.url,
            style = WishlifyTheme.typography.labelLarge,
            color = WishlifyTheme.colorScheme.outline,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
    }
  }
}

@Composable
private fun SelectableWishlist(
  modifier: Modifier,
  wishlist: Wishlist,
  selected: Boolean,
  onClick: () -> Unit,
) {

  val containerColor = if (selected) {
    WishlifyTheme.colorScheme.primaryContainer.copy(alpha = .7f)
  } else {
    WishlifyTheme.colorScheme.surfaceContainerHigh
  }

  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = containerColor,
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      when (val image = wishlist.photo) {
        is ImageMedia.Preset -> {
          val preset = remember(image) { ImagePreset.findById(image.id.toInt()) }
          Image(
            modifier = Modifier
              .size(50.dp)
              .background(
                color = WishlifyTheme.colorScheme.surfaceBright,
                shape = WishlifyTheme.shapes.small,
              ),
            painter = painterResource(preset.res),
            contentDescription = preset.name,
            contentScale = ContentScale.Crop
          )
        }
        is ImageMedia.Url -> {
          RemoteImage(
            modifier = Modifier
              .size(50.dp)
              .clip(WishlifyTheme.shapes.small),
            url = image.url,
            contentScale = ContentScale.Crop
          )
        }
      }

      Spacer(Modifier.width(12.dp))

      Text(
        text = wishlist.title,
        style = WishlifyTheme.typography.bodyLarge,
        color = WishlifyTheme.colorScheme.onSurface
      )
    }
  }
}
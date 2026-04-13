package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SearchBottomSheet
import com.splanes.uoc.wishlify.presentation.common.components.image.ImagePreset
import com.splanes.uoc.wishlify.presentation.common.components.image.RemoteImage
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedWishlistsSearchBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  wishlists: List<SharedWishlist>,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  onClick: (SharedWishlist) -> Unit,
) {
  SearchBottomSheet(
    modifier = modifier,
    visible = visible,
    sheetState = sheetState,
    items = wishlists,
    title = stringResource(R.string.wishlists_search),
    description = stringResource(R.string.wishlists_search_description),
    queryLabel = stringResource(R.string.wishlists_new_list_name_input),
    onDismiss = onDismiss,
    onSearch = { items, query ->
      items.filter { it.linkedWishlist.name.contains(query, ignoreCase = true) }
    },
    onResultClick = onClick,
    resultContent = { result -> ResultRow(wishlist = result.linkedWishlist) }
  )
}

@Composable
private fun ResultRow(wishlist: SharedWishlist.LinkedWishlist) {
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
    text = wishlist.name,
    style = WishlifyTheme.typography.bodyLarge,
    color = WishlifyTheme.colorScheme.onSurface
  )
}
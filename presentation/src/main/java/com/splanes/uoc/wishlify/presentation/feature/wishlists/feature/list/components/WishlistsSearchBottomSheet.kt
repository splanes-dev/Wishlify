package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.image.ImagePreset
import com.splanes.uoc.wishlify.presentation.common.components.image.RemoteImage
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun WishlistsSearchBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  wishlists: List<Wishlist>,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  onWishlistClick: (Wishlist) -> Unit,
) {
  if (visible) {

    val nameState = rememberTextInputState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(nameState) {
      snapshotFlow { nameState.text }
        .debounce(500.milliseconds)
        .distinctUntilChanged()
        .collect { text -> query = text }
    }

    val results by remember {
      derivedStateOf { wishlists.filter { it.title.contains(query, ignoreCase = true) } }
    }

    ModalBottomSheet(
      modifier = modifier,
      sheetState = sheetState,
      onDismissRequest = onDismiss
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = stringResource(R.string.wishlists_search),
            tint = WishlifyTheme.colorScheme.onSurface
          )

          Text(
            text = stringResource(R.string.wishlists_search),
            style = WishlifyTheme.typography.titleLarge,
            color = WishlifyTheme.colorScheme.onSurface
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = stringResource(R.string.wishlists_search_description),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = nameState,
          label = stringResource(R.string.wishlists_new_list_name_input),
          leadingIcon = Icons.Rounded.Search,
          cleanable = false,
          singleLine = true
        )

        AnimatedVisibility(
          modifier = Modifier.padding(top = 8.dp),
          visible = query.isNotBlank(),
          enter = expandVertically(),
          exit = shrinkVertically()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .verticalScroll(rememberScrollState())
          ) {

            HorizontalDivider(
              modifier = Modifier.fillMaxWidth(),
              color = WishlifyTheme.colorScheme.outline
            )

            Spacer(Modifier.height(8.dp))

            Text(
              text = stringResource(R.string.search_results),
              style = WishlifyTheme.typography.titleSmall,
              color = WishlifyTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(16.dp))

            if (results.isNotEmpty()) {
              results.forEach { result ->
                ResultRow(
                  wishlist = result,
                  onClick = { onWishlistClick(result) }
                )
              }
            } else {
              Text(
                text = stringResource(R.string.search_results_no_results_prompt),
                style = WishlifyTheme.typography.bodySmall,
                color = WishlifyTheme.colorScheme.outline
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun ResultRow(
  wishlist: Wishlist,
  onClick: () -> Unit,
) {
  Surface(
    shape = WishlifyTheme.shapes.small,
    color = Color.Transparent,
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          vertical = 4.dp,
          horizontal = 8.dp
        ),
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
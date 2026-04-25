package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.CardImage
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> EventsByGroupBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  title: String,
  description: String,
  items: List<T>,
  itemContent: (T) -> EventsByGroupBottomSheet.ItemContent,
  isFilteredResultsBannerVisible: Boolean,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  onClick: (T) -> Unit,
) {
  if (visible) {
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
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = title,
          style = WishlifyTheme.typography.titleLarge,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(16.dp))

        Text(
          modifier = Modifier.fillMaxWidth(),
          text = description,
          style = WishlifyTheme.typography.titleSmall,
          color = WishlifyTheme.colorScheme.onSurface,
          textAlign = TextAlign.Justify
        )

        if (isFilteredResultsBannerVisible) {
          Spacer(Modifier.height(16.dp))

          FilteredResultsBanner()
        }

        Spacer(Modifier.height(16.dp))

        items.forEachIndexed { index, item ->
          ItemRow(
            content = itemContent(item),
            onClick = { onClick(item) }
          )

          if (index != items.lastIndex) {
            Spacer(Modifier.height(8.dp))
          }
        }

        Spacer(Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun ItemRow(
  content: EventsByGroupBottomSheet.ItemContent,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Surface(
    modifier = modifier,
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
      Box(
        modifier = Modifier
          .size(50.dp)
          .clip(WishlifyTheme.shapes.small)
      ) {
        CardImage(
          modifier = Modifier
            .border(
              width = 1.dp,
              color = WishlifyTheme.colorScheme.outline.copy(alpha = .16f),
              shape = WishlifyTheme.shapes.small
            ),
          media = content.media,
          width = 50.dp,
          placeholder = painterResource(content.placeholder),
          enabled = !content.isFinished
        )
      }

      Spacer(Modifier.width(12.dp))

      Text(
        text = content.name,
        style = WishlifyTheme.typography.bodyLarge,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Spacer(Modifier.weight(1f))

      if (content.isFinished) {
        Surface(
          shape = WishlifyTheme.shapes.extraSmall,
          color = Color.Transparent,
          border = BorderStroke(width = 1.dp, color = WishlifyTheme.colorScheme.outline)
        ) {
          Text(
            modifier = Modifier.padding(4.dp),
            text = stringResource(R.string.finished),
            style = WishlifyTheme.typography.labelSmall,
            color = WishlifyTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@Composable
private fun FilteredResultsBanner() {
  Surface(
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.warningContainer,
  ) {
    Text(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      text = stringResource(R.string.groups_events_by_group_filtered_results_banner),
      style = WishlifyTheme.typography.titleSmall,
      color = WishlifyTheme.colorScheme.onWarningContainer,
    )
  }
}

object EventsByGroupBottomSheet {
  data class ItemContent(
    val media: ImageMedia?,
    @param:DrawableRes val placeholder: Int,
    val name: String,
    val isFinished: Boolean,
  )
}
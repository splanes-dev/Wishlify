package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonCustom
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun <T> FiltersBar(
  filters: List<FiltersBar.Filter<T>>,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
  onFilterClear: (T) -> Unit,
  onFilterClick: (T) -> Unit,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {

    Text(
      modifier = Modifier.padding(contentPadding),
      text = stringResource(R.string.filters),
      style = WishlifyTheme.typography.titleMedium,
      color = WishlifyTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Bold
    )

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = contentPadding,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(filters) { filter ->
        FilterChip(
          selected = filter.selected,
          label = { Text(text = filter.text) },
          leadingIcon = {
            Icon(
              modifier = Modifier.size(20.dp),
              painter = filter.leadingIcon,
              contentDescription = filter.text,
              tint = if (filter.selected) {
                WishlifyTheme.colorScheme.primary
              } else {
                WishlifyTheme.colorScheme.onSurface.copy(alpha = .6f)
              }
            )
          },
          trailingIcon = {
            if (filter.selected) {
              IconButtonCustom(
                imageVector = Icons.Rounded.Close,
                contentSize = DpSize(24.dp, 24.dp),
                onClick = { onFilterClear(filter.item) }
              )
            } else {
              Icon(
                modifier = Modifier.rotate(90f),
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null
              )
            }
          },
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = filter.selected,
            selectedBorderWidth = 1.dp,
            selectedBorderColor = WishlifyTheme.colorScheme.primary
          ),
          onClick = { onFilterClick(filter.item) }
        )
      }
    }
  }
}

object FiltersBar {
  data class Filter<T>(
    val item: T,
    val leadingIcon: Painter,
    val text: String,
    val selected: Boolean,
  )

  @Composable
  @Stable
  fun <T> filterOf(
    item: T,
    leadingIcon: ImageVector,
    text: String,
    selected: Boolean,
  ) = Filter(
    item,
    rememberVectorPainter(leadingIcon),
    text,
    selected
  )
}
package com.splanes.uoc.wishlify.presentation.common.components.filters

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> FilterBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  title: String,
  options: List<FilterBottomSheet.Option<T>>,
  selected: List<FilterBottomSheet.Option<T>>,
  allowMultiChoice: Boolean,
  modifier: Modifier = Modifier,
  description: String? = null,
  onDismiss: () -> Unit,
  onApplyFilter: (List<T>) -> Unit,
) {

  val optionsSelected = remember(selected) { mutableStateListOf(*selected.toTypedArray()) }

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

        Spacer(modifier = Modifier.height(16.dp))

        description?.let {
          Text(
            modifier = Modifier.fillMaxWidth(),
            text = description,
            style = WishlifyTheme.typography.bodyMedium,
            color = WishlifyTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(16.dp))
        }

        options.forEachIndexed { index, option ->

          if (index != 0) {
            HorizontalDivider(
              modifier = Modifier.fillMaxWidth(),
              color = WishlifyTheme.colorScheme.outline.copy(alpha = .33f)
            )
          }

          OptionItem(
            modifier = Modifier.padding(vertical = 2.dp),
            text = option.text,
            selected = option in optionsSelected,
            onClick = {
              when {
                optionsSelected.contains(option) -> optionsSelected.remove(option)
                allowMultiChoice -> optionsSelected.add(option)
                else -> optionsSelected.apply {
                  clear()
                  add(option)
                }
              }
            }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          onClick = { onApplyFilter(optionsSelected.map { it.item }) }
        ) {
          ButtonText(text = stringResource(R.string.apply_filter))
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable

private fun OptionItem(
  modifier: Modifier,
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
) {

  val contentColor = if (selected) {
    WishlifyTheme.colorScheme.onPrimaryContainer
  } else {
    WishlifyTheme.colorScheme.onSurface
  }

  val scale by animateFloatAsState(
    if (selected) {
      1.05f
    } else {
      1f
    }
  )

  Surface(
    modifier = modifier.graphicsLayer {
      scaleX = scale
      scaleY = scale
    },
    shape = WishlifyTheme.shapes.small,
    color = if (selected) {
      WishlifyTheme.colorScheme.primaryContainer
    } else {
      Color.Transparent
    },
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          vertical = 4.dp,
          horizontal = 8.dp
        ),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

      Text(
        modifier = Modifier.weight(1f),
        text = text,
        style = WishlifyTheme.typography.bodyMedium,
        color = contentColor
      )

      if (selected) {
        Icon(
          imageVector = Icons.Rounded.Done,
          contentDescription = null,
          tint = contentColor
        )
      }
    }
  }
}

object FilterBottomSheet {
  data class Option<T>(
    val item: T,
    val text: String,
  )
}
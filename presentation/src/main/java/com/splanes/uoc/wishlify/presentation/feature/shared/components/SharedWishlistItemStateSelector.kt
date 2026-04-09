package com.splanes.uoc.wishlify.presentation.feature.shared.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.presentation.common.components.button.ToggleButtonShape
import com.splanes.uoc.wishlify.presentation.feature.shared.model.SharedWishlistItemStateAction
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.border
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.colors
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.icon
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.name
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.supportingText
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistItemStateSelector(
  state: SharedWishlistItem.State,
  actions: List<SharedWishlistItemStateAction>,
  modifier: Modifier = Modifier,
  onActionSelected: (action: SharedWishlistItemStateAction?) -> Unit
) {

  val resources = LocalResources.current
  var selected: SharedWishlistItemStateAction? by remember(state) { mutableStateOf(null) }
  val isSupportingTextVisible by remember(selected) {
    derivedStateOf { selected?.supportingText(resources) != null }
  }

  AnimatedVisibility(
    modifier = modifier,
    visible = actions.isNotEmpty(),
    enter = expandVertically(),
    exit = shrinkVertically()
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        itemVerticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        actions.forEach { action ->
          val checked = action == selected
          OutlinedToggleButton(
            shapes = ToggleButtonShape,
            checked = checked,
            onCheckedChange = {
              selected = action.takeIf { !checked }
              onActionSelected(selected)
            },
            colors = action.colors(),
            border = action.border()
          ) {
            Icon(
              painter = action.icon(),
              contentDescription = action.name(),
            )

            Spacer(Modifier.width(4.dp))

            Text(
              text = action.name(),
              style = WishlifyTheme.typography.labelSmall,
            )
          }
        }
      }

      AnimatedVisibility(
        visible = isSupportingTextVisible,
        enter = expandVertically(),
        exit = shrinkVertically()
      ) {
        Text(
          text = selected?.supportingText(resources) ?: AnnotatedString(""),
          style = WishlifyTheme.typography.labelSmall,
          color = WishlifyTheme.colorScheme.onSurface,
          textAlign = TextAlign.Justify
        )
      }
    }
  }
}
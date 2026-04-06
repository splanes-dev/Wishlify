package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.rounded.BorderColor
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories.model.CategoryAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.model.CategoryUiModel
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CategoryItem(
  category: CategoryUiModel,
  modifier: Modifier = Modifier,
  onAction: (CategoryAction) -> Unit,
) {

  var expanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Surface(
      modifier = Modifier.heightIn(min = 70.dp),
      color = Color.Transparent,
      shape = WishlifyTheme.shapes.small,
      onClick = { expanded = !expanded }
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {

        Icon(
          modifier = Modifier.size(36.dp),
          imageVector = Icons.Filled.Circle,
          contentDescription = category.name,
          tint = category.color
        )

        Spacer(Modifier.width(16.dp))

        Text(
          modifier = Modifier.weight(1f),
          text = category.name,
          style = WishlifyTheme.typography.titleMedium,
          color = WishlifyTheme.colorScheme.onSurface
        )

        AnimatedVisibility(
          visible = expanded,
          enter = slideInHorizontally(
            initialOffsetX = { it }
          ) + fadeIn(),
          exit = slideOutHorizontally(
            targetOffsetX = { it }
          ) + fadeOut()
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedIconButton(
              shapes = IconButtonShape,
              onClick = {
                onAction(CategoryAction.Edit(category))
                expanded = false
              },
              border = BorderStroke(
                width = 1.dp,
                color = WishlifyTheme.colorScheme.primary
              ),
              colors = IconButtonDefaults.outlinedIconButtonColors(
                contentColor = WishlifyTheme.colorScheme.primary
              )
            ) {
              Icon(
                imageVector = Icons.Rounded.BorderColor,
                contentDescription = stringResource(R.string.edit)
              )
            }

            OutlinedIconButton(
              shapes = IconButtonShape,
              onClick = {
                onAction(CategoryAction.Delete(category))
                expanded = false
              },
              border = BorderStroke(
                width = 1.dp,
                color = WishlifyTheme.colorScheme.error
              ),
              colors = IconButtonDefaults.outlinedIconButtonColors(
                contentColor = WishlifyTheme.colorScheme.error
              )
            ) {
              Icon(
                imageVector = Icons.Outlined.DeleteForever,
                contentDescription = stringResource(R.string.delete)
              )
            }
          }
        }
      }
    }

    HorizontalDivider(
      modifier = Modifier.fillMaxWidth(),
      color = WishlifyTheme.colorScheme.outline
    )
  }
}
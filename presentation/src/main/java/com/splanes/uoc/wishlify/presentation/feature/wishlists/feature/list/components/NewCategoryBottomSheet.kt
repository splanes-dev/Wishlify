package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewCategoryBottomSheet(
  isVisible: Boolean,
  sheetState: SheetState,
  error: String?,
  initial: Category? = null,
  onClearInputError: () -> Unit,
  onDismiss: () -> Unit,
  onCreate: (name: String, color: Category.CategoryColor) -> Unit,
) {
  if (isVisible) {
    val nameInputState = rememberTextInputState(
      initialValue = initial?.name.orEmpty(),
      onClearError = onClearInputError
    )

    var colorSelected by remember {
      mutableStateOf(initial?.color ?: Category.CategoryColor.Purple)
    }

    val isButtonEnabled by remember {
      derivedStateOf {
        nameInputState.text.isNotBlank()
      }
    }

    LaunchedEffect(error) {
      nameInputState.error(error)
    }

    ModalBottomSheet(
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
          text = stringResource(
            if (initial != null) {
              R.string.wishlists_update_category
            } else {
              R.string.wishlists_create_category
            }
          ),
          style = WishlifyTheme.typography.titleLarge,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = nameInputState,
          leadingIcon = Icons.Rounded.Sell,
          label = stringResource(R.string.wishlists_category),
          singleLine = true,
          maxLines = 1,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          modifier = Modifier.fillMaxWidth(),
          text = stringResource(R.string.wishlists_create_category_color_description),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        ColorPicker(
          selected = colorSelected,
          onChange = { colorSelected = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = {
            onCreate(
              nameInputState.text,
              colorSelected
            )
          },
        ) {
          ButtonText(
            text = stringResource(
              if (initial != null) {
                R.string.edit
              } else {
                R.string.create
              }
            )
          )
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun ColorPicker(
  selected: Category.CategoryColor,
  modifier: Modifier = Modifier,
  onChange: (Category.CategoryColor) -> Unit,
) {
  FlowRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    itemVerticalAlignment = Alignment.CenterVertically,
  ) {
    Category.CategoryColor.entries.forEach { color ->
      val isSelected = selected == color
      Surface(
        modifier = Modifier.padding(4.dp),
        border = if (isSelected) {
          BorderStroke(
            width = 1.dp,
            color = WishlifyTheme.colorScheme.primary
          )
        } else {
          null
        },
        color = if (isSelected) {
          WishlifyTheme.colorScheme.surfaceBright
        } else {
          Color.Transparent
        },
        shape = CircleShape,
        onClick = { onChange(color) },
      ) {
        Icon(
          modifier = Modifier.size(36.dp),
          imageVector = Icons.Filled.Circle,
          contentDescription = color.name,
          tint = color.color().let { c ->
            if (!isSelected) {
              c.copy(alpha = .6f)
            } else {
              c
            }
          },
        )
      }
    }
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
package com.splanes.uoc.wishlify.presentation.common.components.input.dropdown

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DropdownInput(
  items: List<DropdownInput.Item>,
  label: String,
  modifier: Modifier = Modifier,
  initial: DropdownInput.Option? = null,
  leadingIcon: ImageVector? = null,
  showButtonSpacer: Boolean = true,
  supportingText: String = "",
  allowUnselect: Boolean = true,
  onSelectionChanged: (Int?) -> Unit,
  onAdd: ((id: Int) -> Unit)? = null,
) {
  val density = LocalDensity.current
  var expanded by remember { mutableStateOf(false) }
  var widthPx by remember { mutableIntStateOf(0) }
  val widthDp by remember { derivedStateOf { with(density) { widthPx.toDp() } } }
  val textFieldState = rememberTextFieldState(initial?.text.orEmpty())
  var selected by remember(initial) { mutableStateOf(initial) }

  LaunchedEffect(initial) {
    textFieldState.setTextAndPlaceCursorAtEnd(initial?.text.orEmpty())
  }

  ExposedDropdownMenuBox(
    modifier = modifier,
    expanded = expanded,
    onExpandedChange = { expanded = it },
  ) {
    TextField(
      modifier = modifier
        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        .onGloballyPositioned { coordinates -> widthPx = coordinates.size.width },
      state = textFieldState,
      readOnly = true,
      lineLimits = TextFieldLineLimits.SingleLine,
      label = { Text(text = label) },
      leadingIcon = leadingIcon?.let { icon ->
        @Composable {
          Icon(
            imageVector = icon,
            contentDescription = null
          )
        }
      },
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
      supportingText = supportingText
        .takeUnless { it.isBlank() }
        ?.let { support ->
          @Composable {
            Text(
              text = support,
              color = WishlifyTheme.colorScheme.onSurface.copy(alpha = .7f)
            )
          }
        },
      colors = TextFieldDefaults.colors(
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
      ),
      shape = MaterialTheme.shapes.small
    )

    DropdownMenuPopup(
      modifier = Modifier.width(widthDp),
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      val options = remember(items) { items.filterIsInstance<DropdownInput.Option>() }
      val buttons = remember(items) { items.filterIsInstance<DropdownInput.Button>() }

      DropdownMenuGroup(shapes = MenuDefaults.groupShapes()) {
        options.forEachIndexed { index, option ->
          DropdownMenuItem(
            shapes = MenuDefaults.itemShape(index, options.count()),
            text = { Text(option.text, style = MaterialTheme.typography.bodyLarge) },
            selected = option == selected,
            onClick = {
              if (allowUnselect || option != selected) {
                selected = option.takeIf { selected != it }
                if (selected != null) {
                  textFieldState.setTextAndPlaceCursorAtEnd(selected?.text.orEmpty())
                } else {
                  textFieldState.clearText()
                }
                onSelectionChanged(selected?.id)
              }
            },
            leadingIcon = option.leadingIcon?.let { icon ->
              @Composable {
                Icon(
                  modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                  painter = icon,
                  contentDescription = null,
                )
              }
            },
            trailingIcon = option.trailingIcon?.let { icon ->
              @Composable {
                Icon(
                  modifier = Modifier.size(MenuDefaults.TrailingIconSize),
                  painter = icon,
                  contentDescription = null,
                  tint = option.trailingIconColor ?: LocalContentColor.current
                )
              }
            },
            selectedLeadingIcon = {
              Icon(
                imageVector = Icons.Filled.Check,
                modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                contentDescription = null,
              )
            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
          )
        }
      }

      if (buttons.isNotEmpty() && showButtonSpacer) {
        Spacer(Modifier.height(8.dp))
      }

      DropdownMenuGroup(shapes = MenuDefaults.groupShapes()) {
        buttons.forEachIndexed { index, button ->
          DropdownMenuItem(
            shapes = MenuDefaults.itemShape(index, buttons.count()),
            text = {
              Text(button.text, style = MaterialTheme.typography.bodyLarge)
            },
            selected = false,
            onClick = { onAdd?.invoke(button.id) },
            leadingIcon = button.leadingIcon?.let { icon ->
              @Composable {
                Icon(
                  modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                  painter = icon,
                  contentDescription = null,
                )
              }
            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
          )
        }
      }
    }
  }
}

object DropdownInput {
  sealed interface Item {
    val id: Int
    val text: String
  }

  data class Option(
    override val id: Int,
    override val text: String,
    val leadingIcon: Painter? = null,
    val trailingIcon: Painter? = null,
    val trailingIconColor: Color? = null
  ) : Item

  data class Button(
    override val id: Int,
    override val text: String,
    val leadingIcon: Painter? = null,
  ) : Item
}
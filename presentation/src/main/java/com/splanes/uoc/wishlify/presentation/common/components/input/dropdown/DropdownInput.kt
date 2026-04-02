package com.splanes.uoc.wishlify.presentation.common.components.input.dropdown

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DropdownInput(
  items: List<DropdownInput.Item>,
  label: String,
  modifier: Modifier = Modifier,
  initial: DropdownInput.Option? = null,
  leadingIcon: ImageVector? = null,
  showButtonDivider: Boolean = true,
  supportingText: String = "",
  onSelectionChanged: (Int?) -> Unit,
  onAdd: ((id: Int) -> Unit)? = null,
) {
  var expanded by remember { mutableStateOf(false) }
  val textFieldState = rememberTextFieldState(initial?.text.orEmpty())
  var selected by remember { mutableStateOf(initial) }

  ExposedDropdownMenuBox(
    modifier = modifier,
    expanded = expanded,
    onExpandedChange = { expanded = it },
  ) {
    TextField(
      modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
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

    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      containerColor = MenuDefaults.groupStandardContainerColor,
      shape = MenuDefaults.standaloneGroupShape,
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
              selected = option.takeIf { selected != it }
              if (selected != null) {
                textFieldState.setTextAndPlaceCursorAtEnd(selected?.text.orEmpty())
              } else {
                textFieldState.clearText()
              }
              onSelectionChanged(selected?.id)
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

      if (buttons.isNotEmpty() && showButtonDivider) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
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
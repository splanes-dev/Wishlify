package com.splanes.uoc.wishlify.presentation.common.components.input.text


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun TextInput(
  state: TextInputState,
  leadingIcon: ImageVector,
  modifier: Modifier = Modifier,
  label: String = "",
  cleanable: Boolean = true,
  readOnly: Boolean = false,
  trailingIcon: (@Composable () -> Unit)? = null,
  enabled: Boolean = true,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  singleLine: Boolean = false,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  minLines: Int = 1,
) {
  TextInput(
    state = state,
    leadingIcon = rememberVectorPainter(leadingIcon),
    modifier = modifier,
    label = label,
    cleanable = cleanable,
    readOnly = readOnly,
    trailingIcon = trailingIcon,
    enabled = enabled,
    visualTransformation = visualTransformation,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    singleLine = singleLine,
    maxLines = maxLines,
    minLines = minLines,
  )
}

@Composable
fun TextInput(
  state: TextInputState,
  leadingIcon: Painter,
  modifier: Modifier = Modifier,
  label: String = "",
  cleanable: Boolean = true,
  readOnly: Boolean = false,
  trailingIcon: (@Composable () -> Unit)? = null,
  enabled: Boolean = true,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  singleLine: Boolean = false,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  minLines: Int = 1,
) {
  var isFocused by remember { mutableStateOf(false) }

  TextField(
    modifier = modifier.onFocusEvent { focusState ->
      isFocused = focusState.isFocused
    },
    value = state.text,
    onValueChange = state::onValueChanged,
    label = { Text(text = label) },
    leadingIcon = {
      Icon(
        painter = leadingIcon,
        contentDescription = label,
        tint = when {
          state.isError -> WishlifyTheme.colorScheme.error
          isFocused -> WishlifyTheme.colorScheme.primary
          else -> WishlifyTheme.colorScheme.onSurface
        }.copy(alpha = .7f)
      )
    },
    trailingIcon = when {
      trailingIcon != null -> trailingIcon
      cleanable -> {
        {
          AnimatedVisibility(
            visible = state.text.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            IconButton(onClick = state::onClear) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Clear",
                tint = when {
                  state.isError -> WishlifyTheme.colorScheme.error
                  isFocused -> WishlifyTheme.colorScheme.primary
                  else -> WishlifyTheme.colorScheme.onSurface
                }.copy(alpha = .5f),
              )
            }
          }
        }
      }

      else -> null
    },
    supportingText = state.support
      .takeUnless { it.isBlank() }
      ?.let { support ->
        {
          Text(
            text = support,
            color = when {
              state.isError -> WishlifyTheme.colorScheme.error
              isFocused -> WishlifyTheme.colorScheme.primary
              else -> WishlifyTheme.colorScheme.onSurface
            }.copy(alpha = .7f)
          )
        }
      },
    readOnly = readOnly,
    enabled = enabled,
    isError = state.isError,
    visualTransformation = visualTransformation,
    keyboardActions = keyboardActions,
    keyboardOptions = keyboardOptions,
    maxLines = maxLines,
    minLines = minLines,
    singleLine = singleLine,
    colors = TextFieldDefaults.colors(
      disabledIndicatorColor = Color.Transparent,
      errorIndicatorColor = Color.Transparent,
      focusedIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent,
    ),
    shape = MaterialTheme.shapes.small
  )
}

@Composable
@PreviewLightDark
private fun TextInputPreview() {
  WishlifyTheme {
    val state = rememberTextInputState()

    Column {
      TextInput(
        state = state,
        leadingIcon = Icons.Rounded.Email,
        label = "Label",
      )

      Button(onClick = {
        state.validate { if (it.isBlank()) "Required" else null }
      }) {
        Text("Validate")
      }
    }
  }
}
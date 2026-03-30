package com.splanes.uoc.wishlify.presentation.common.components.input.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInputState

@Composable
fun PasswordInput(
  state: TextInputState,
  leadingIcon: ImageVector,
  modifier: Modifier = Modifier,
  label: String = "",
  enabled: Boolean = true,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  singleLine: Boolean = false,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  minLines: Int = 1,
) {
  var isPasswordVisible by remember { mutableStateOf(false) }
  var isFocused by remember { mutableStateOf(false) }

  TextInput(
    modifier = modifier.onFocusEvent { focusState -> isFocused = focusState.isFocused },
    state = state,
    leadingIcon = leadingIcon,
    label = label,
    enabled = enabled,
    trailingIcon = {
      AnimatedVisibility(
        visible = state.text.isNotBlank() && isFocused,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        IconButton(
          onClick = { isPasswordVisible = !isPasswordVisible }
        ) {
          Icon(
            imageVector = if (isPasswordVisible) {
              Icons.Rounded.VisibilityOff
            } else {
              Icons.Rounded.Visibility
            },
            contentDescription = if (isPasswordVisible) {
              "Hide password"
            } else {
              "Show password"
            },
          )
        }
      }
    },
    visualTransformation = if (isPasswordVisible) {
      VisualTransformation.None
    } else {
      PasswordVisualTransformation()
    },
    keyboardActions = keyboardActions,
    keyboardOptions = keyboardOptions,
    singleLine = singleLine,
    maxLines = maxLines,
    minLines = minLines,
  )
}
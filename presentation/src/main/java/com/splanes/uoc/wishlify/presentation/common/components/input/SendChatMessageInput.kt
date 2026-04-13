package com.splanes.uoc.wishlify.presentation.common.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Textsms
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SendChatMessageInput(
  modifier: Modifier = Modifier,
  onSend: (String) -> Unit
) {

  val textState = rememberTextInputState()
  val isButtonEnabled by remember { derivedStateOf { textState.text.isNotBlank() } }

  val send = {
    onSend(textState.text)
    textState.onClear()
  }

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    TextInput(
      modifier = Modifier.weight(1f),
      state = textState,
      label = "",
      placeholder = stringResource(R.string.chat_message_placeholder),
      leadingIcon = Icons.Outlined.Textsms,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
      keyboardActions = KeyboardActions(onSend = { if (isButtonEnabled) send() }),
      singleLine = false,
      maxLines = 3
    )

    IconButton(
      modifier = Modifier.size(56.dp),
      shapes = IconButtonShape,
      enabled = isButtonEnabled,
      onClick = send
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Outlined.Send,
        contentDescription = null
      )
    }
  }
}
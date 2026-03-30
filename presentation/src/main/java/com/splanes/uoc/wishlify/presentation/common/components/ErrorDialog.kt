package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ErrorDialog(
  uiModel: ErrorUiModel,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  onAction: () -> Unit = {},
) {
  AlertDialog(
    modifier = modifier,
    icon = {
      Icon(
        imageVector = uiModel.icon,
        contentDescription = uiModel.title,
        tint = WishlifyTheme.colorScheme.error
      )
    },
    title = { Text(text = uiModel.title) },
    text = { Text(text = uiModel.description, textAlign = TextAlign.Justify) },
    onDismissRequest = onDismiss,
    confirmButton = {
      if (uiModel.actionButton != null) {
        OutlinedButton(
          shapes = ButtonShape,
          onClick = onDismiss,
        ) {
          ButtonText(text = uiModel.dismissButton)
        }
      } else {
        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          onClick = onDismiss,
        ) {
          ButtonText(text = uiModel.dismissButton)
        }
      }

    },
    dismissButton = uiModel.actionButton?.let { action ->
      @Composable {
        Button(
          shapes = ButtonShape,
          onClick = {
            onAction()
            onDismiss()
          },
        ) {
          ButtonText(text = action)
        }
      }
    },
  )
}
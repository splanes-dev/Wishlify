package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConfirmationDialog(
  modifier: Modifier = Modifier,
  title: String = stringResource(R.string.error_dialog_title_warning),
  description: String = stringResource(R.string.confirmation_dialog_description),
  icon: ImageVector = Icons.Rounded.WarningAmber,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
) {
  AlertDialog(
    modifier = modifier,
    icon = {
      Icon(
        modifier = Modifier.size(36.dp),
        imageVector = icon,
        contentDescription = title,
        tint = WishlifyTheme.colorScheme.warning
      )
    },
    title = { Text(text = title) },
    text = { Text(text = description, textAlign = TextAlign.Justify) },
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        shapes = ButtonShape,
        colors = ButtonDefaults.buttonColors(
          containerColor = WishlifyTheme.colorScheme.error,
          contentColor = WishlifyTheme.colorScheme.onError,
        ),
        onClick = {
          onConfirm()
          onDismiss()
        },
      ) {
        ButtonText(text = stringResource(R.string.confirm))
      }
    },
    dismissButton = {
      OutlinedButton(
        shapes = ButtonShape,
        onClick = onDismiss,
      ) {
        ButtonText(text = stringResource(R.string.cancel))
      }
    }
  )
}
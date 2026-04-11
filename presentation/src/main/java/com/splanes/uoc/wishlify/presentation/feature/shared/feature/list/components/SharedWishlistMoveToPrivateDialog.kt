package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistMoveToPrivateDialog(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
) {
  AlertDialog(
    icon = {
      Icon(
        modifier = Modifier.size(36.dp),
        imageVector = Icons.Rounded.WarningAmber,
        contentDescription = null,
        tint = WishlifyTheme.colorScheme.info
      )
    },
    title = {
      Text(text = stringResource(R.string.shared_wishlists_back_to_private_dialog_title))
    },
    text = {
      Text(
        text = stringResource(R.string.shared_wishlists_back_to_private_dialog_description),
        textAlign = TextAlign.Justify
      )
    },
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        shapes = ButtonShape,
        colors = ButtonDefaults.buttonColors(
          containerColor = WishlifyTheme.colorScheme.primary,
          contentColor = WishlifyTheme.colorScheme.onPrimary,
        ),
        onClick = {
          onConfirm()
          onDismiss()
        },
      ) {
        ButtonText(text = stringResource(R.string.btn_continue))
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
package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SmokeFeatureDialog(
  modifier: Modifier = Modifier,
  title: String = stringResource(R.string.feature_not_yet_available_title),
  description: AnnotatedString = htmlString(R.string.feature_not_yet_available_description),
  onDismiss: () -> Unit,
  onAnswer: (Boolean) -> Unit,
) {
  AlertDialog(
    modifier = modifier,
    icon = {
      Icon(
        modifier = Modifier.size(36.dp),
        imageVector = Icons.Rounded.Construction,
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
        onClick = {
          onAnswer(true)
          onDismiss()
        },
      ) {
        ButtonText(text = stringResource(R.string.feature_not_yet_available_positive_btn))
      }
    },
    dismissButton = {
      OutlinedButton(
        shapes = ButtonShape,
        onClick = {
          onAnswer(false)
          onDismiss()
        },
      ) {
        ButtonText(text = stringResource(R.string.feature_not_yet_available_negative_btn))
      }
    }
  )
}
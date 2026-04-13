package com.splanes.uoc.wishlify.presentation.common.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadMoreChatButton(
  isLoading: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Row(
    modifier = modifier.heightIn(min = 40.dp),
    horizontalArrangement = Arrangement.Center,
  ) {
    if (isLoading) {
      LoadingIndicator(modifier = Modifier.size(40.dp))
    } else {
      TextButton(
        shapes = ButtonShape,
        onClick = onClick
      ) {
        ButtonText(text = stringResource(R.string.chat_load_more_btn))
      }
    }
  }
}
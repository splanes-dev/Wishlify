package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewItemFromLinkBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  error: String?,
  onClearInputError: () -> Unit,
  onCreate: (String) -> Unit,
  onDismiss: () -> Unit,
) {

  if (visible) {
    val linkState = rememberTextInputState(onClearError = onClearInputError)
    val isButtonEnabled by remember { derivedStateOf { linkState.text.isNotBlank() } }

    LaunchedEffect(error) {
      linkState.error(error)
    }

    ModalBottomSheet(
      sheetState = sheetState,
      onDismissRequest = onDismiss
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      ) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = stringResource(R.string.wishlists_detail_new_item),
          style = WishlifyTheme.typography.titleLarge,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          modifier = Modifier.fillMaxWidth(),
          text = stringResource(R.string.wishlists_detail_new_item_link_modal_description),
          style = WishlifyTheme.typography.titleSmall,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = linkState,
          leadingIcon = Icons.Rounded.Link,
          label = stringResource(R.string.wishlists_detail_link),
          singleLine = true,
          maxLines = 1,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = { onCreate(linkState.text) },
        ) {
          ButtonText(text = stringResource(R.string.create))
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
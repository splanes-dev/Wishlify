package com.splanes.uoc.wishlify.presentation.feature.profile.feature.hobbies.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Surfing
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonCustom
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileHobbiesInput(
  enabled: Boolean,
  values: List<String>,
  modifier: Modifier = Modifier,
  onAdd: (String) -> Unit,
  onRemove: (String) -> Unit,
) {

  var isNewHobbyModalOpen by remember { mutableStateOf(false) }
  val newHobbySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  Column(modifier = modifier) {
    Text(
      text = stringResource(R.string.profile_hobbies_section_title),
      style = WishlifyTheme.typography.titleMedium,
      color = WishlifyTheme.colorScheme.onSurface
    )

    Spacer(Modifier.height(16.dp))

    FlowRow(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      values.forEach { hobby ->
        Hobby(
          enabled = enabled,
          text = hobby,
          onRemove = { onRemove(hobby) }
        )
      }
    }

    Spacer(Modifier.height(8.dp))

    Button(
      enabled = enabled,
      shapes = ButtonShape,
      onClick = { isNewHobbyModalOpen = true },
    ) {

      Icon(
        imageVector = Icons.Rounded.Add,
        contentDescription = null,
      )

      ButtonText(
        modifier = Modifier.padding(horizontal = 8.dp),
        text = stringResource(R.string.add),
        style = WishlifyTheme.typography.labelLarge
      )
    }
  }

  NewHobbyBottomSheet(
    isVisible = isNewHobbyModalOpen,
    sheetState = newHobbySheetState,
    hobbies = values,
    onDismiss = { isNewHobbyModalOpen = false },
    onAdd = onAdd
  )
}

@Composable
private fun Hobby(
  enabled: Boolean,
  text: String,
  onRemove: () -> Unit,
) {
  Surface(
    shape = WishlifyTheme.shapes.small,
    color = if (enabled) {
      WishlifyTheme.colorScheme.surfaceContainer
    } else {
      WishlifyTheme.colorScheme.surfaceContainer.copy(alpha = .33f)
    }
  ) {
    Row(
      modifier = Modifier.padding(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = text,
        style = WishlifyTheme.typography.labelLarge,
        color = WishlifyTheme.colorScheme.onSurface,
      )

      IconButtonCustom(
        imageVector = Icons.Rounded.Close,
        enabled = enabled,
        contentSize = DpSize(24.dp, 24.dp),
        onClick = onRemove
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NewHobbyBottomSheet(
  isVisible: Boolean,
  sheetState: SheetState,
  hobbies: List<String>,
  onDismiss: () -> Unit,
  onAdd: (String) -> Unit,
) {
  if (isVisible) {
    val hobbyInputState = rememberTextInputState()
    val isButtonEnabled by remember {
      derivedStateOf {
        val text = hobbyInputState.text
        text.isNotBlank()
            && text.count() in 3..30
            && hobbies.none { it.equals(text, ignoreCase = true) }
      }
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
          text = stringResource(R.string.profile_hobbies_add_hobby),
          style = WishlifyTheme.typography.titleLarge,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = hobbyInputState,
          leadingIcon = Icons.Rounded.Surfing,
          label = stringResource(R.string.profile_hobbies_add_hobby_label),
          singleLine = true,
          maxLines = 1,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = {
            onAdd(hobbyInputState.text)
            hobbyInputState.onClear()
          },
        ) {
          ButtonText(text = stringResource(R.string.add))
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
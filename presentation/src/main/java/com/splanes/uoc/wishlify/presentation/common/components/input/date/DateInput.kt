package com.splanes.uoc.wishlify.presentation.common.components.input.date

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInputState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DateInput(
  state: TextInputState,
  label: String,
  modifier: Modifier = Modifier,
  valueMillis: Long? = null,
  enabled: Boolean = true,
  onValueChanged: (Long?) -> Unit,
) {

  var isPickerOpen by remember { mutableStateOf(false) }

  TextInput(
    modifier = modifier,
    state = state,
    readOnly = true,
    enabled = enabled,
    singleLine = true,
    label = label,
    leadingIcon = Icons.Rounded.Event,
    trailingIcon = {
      IconButton(
        shapes = IconButtonShape,
        onClick = { if (enabled) isPickerOpen = true }
      ) {
        Icon(
          imageVector = Icons.Rounded.CalendarMonth,
          contentDescription = null
        )
      }
    }
  )

  if (isPickerOpen) {
    val datePickerState = rememberDatePickerState(
      initialSelectedDateMillis = valueMillis
    )

    DatePickerDialog(
      onDismissRequest = { isPickerOpen = false },
      confirmButton = {
        TextButton(
          onClick = {
            onValueChanged(datePickerState.selectedDateMillis)
            isPickerOpen = false
          }
        ) {
          ButtonText(stringResource(R.string.confirm))
        }
      },
      dismissButton = {
        TextButton(
          onClick = { isPickerOpen = false }
        ) {
          ButtonText(stringResource(R.string.cancel))
        }
      }
    ) {
      DatePicker(
        state = datePickerState,
        showModeToggle = true
      )
    }
  }
}
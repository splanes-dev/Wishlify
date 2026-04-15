package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EuroSymbol
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.input.date.DateInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventForm
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventFormUiErrors
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaNewEventBasicsForm(
  form: SecretSantaNewEventForm,
  errors: SecretSantaNewEventFormUiErrors,
  modifier: Modifier = Modifier,
  onClearInputError: (SecretSantaNewEventForm.Input) -> Unit,
  onNext: (form: SecretSantaNewEventForm) -> Unit,
) {

  var imageSelected: ImagePicker.Resource? by remember { mutableStateOf(null) }
  val nameState = rememberTextInputState(
    onClearError = { onClearInputError(SecretSantaNewEventForm.Input.Name) }
  )
  val budgetState = rememberTextInputState(
    onClearError = { onClearInputError(SecretSantaNewEventForm.Input.Budget) }
  )
  var isBudgetApproximate by remember { mutableStateOf(form.isBudgetApproximate) }
  val dateState = rememberTextInputState(
    onClearError = { onClearInputError(SecretSantaNewEventForm.Input.Deadline) }
  )
  var dateMillis: Long? by remember { mutableStateOf(null) }

  val isButtonEnabled by remember {
    derivedStateOf {
      nameState.text.isNotBlank() &&
          budgetState.text.isNotBlank() &&
          dateState.text.isNotBlank()
    }
  }

  LaunchedEffect(dateMillis) {
    dateState.onValueChanged(dateMillis?.formatted().orEmpty())
  }

  LaunchedEffect(form) {
    imageSelected = form.photo
    nameState.onValueChanged(form.name)
    budgetState.onValueChanged(form.budget.takeUnless { it == 0.0 }?.toString().orEmpty())
    isBudgetApproximate = form.isBudgetApproximate
    dateMillis = form.deadline.takeUnless { it == 0L }
  }

  LaunchedEffect(errors) {
    nameState.error(errors.name)
    budgetState.error(errors.budget)
    dateState.error(errors.deadline)
  }

  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {

    Column(
      modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      ImagePicker(
        modifier = Modifier
          .width(135.dp)
          .height(122.dp),
        initial = form.photo,
        preset = emptyList(),
        onSelectionChanged = { image -> imageSelected = image }
      )

      Text(
        modifier = Modifier.padding(start = 8.dp),
        text = stringResource(R.string.optional),
        style = WishlifyTheme.typography.bodySmall,
        color = WishlifyTheme.colorScheme.onSurface.copy(alpha = .7f)
      )
    }

    TextInput(
      modifier = Modifier.fillMaxWidth(),
      state = nameState,
      label = stringResource(R.string.secret_santa_new_event_name_input_label),
      leadingIcon = painterResource(R.drawable.ic_secret_santa),
      singleLine = true
    )

    TextInput(
      modifier = Modifier.fillMaxWidth(),
      state = budgetState,
      label = stringResource(R.string.secret_santa_new_event_budget_input_label),
      leadingIcon = Icons.Rounded.EuroSymbol,
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = stringResource(R.string.secret_santa_new_event_budget_approx_input_label),
        style = WishlifyTheme.typography.bodyLarge,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Switch(
        checked = isBudgetApproximate,
        onCheckedChange = { isBudgetApproximate = it }
      )
    }

    DateInput(
      modifier = Modifier.fillMaxWidth(),
      state = dateState,
      label = stringResource(R.string.secret_santa_new_event_date_limit_label),
      valueMillis = dateMillis,
      onValueChanged = { millis -> dateMillis = millis }
    )

    Spacer(Modifier.weight(1f))

    Button(
      modifier = Modifier.fillMaxWidth(),
      shapes = ButtonShape,
      enabled = isButtonEnabled,
      onClick = {
        val updated = form.copy(
          photo = imageSelected,
          name = nameState.text,
          budget = budgetState.text.toDoubleOrNull() ?: 0.0,
          isBudgetApproximate = isBudgetApproximate,
          deadline = dateMillis ?: 0L
        )
        onNext(updated)
      }
    ) {
      ButtonText(text = stringResource(R.string.next))
    }
  }
}
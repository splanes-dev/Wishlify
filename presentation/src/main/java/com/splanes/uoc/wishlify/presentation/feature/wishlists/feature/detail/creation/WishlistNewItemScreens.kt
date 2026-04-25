package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.creation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.input.dropdown.DropdownInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemForm
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.color
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.icon
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.name
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WishlistNewItemScreen(
  uiState: WishlistNewItemUiState,
  onCreate: (WishlistItemForm) -> Unit,
  onAutocompleteByLink: (url: String) -> Unit,
  onClearInputError: (WishlistItemForm.Input) -> Unit,
  onDismissError: () -> Unit,
  onCancel: () -> Unit,
) {

  val nameState = rememberTextInputState(
    initialValue = uiState.form.name,
    onClearError = { onClearInputError(WishlistItemForm.Input.Name) }
  )

  val storeState = rememberTextInputState(
    initialValue = uiState.form.store,
    supportingText = stringResource(R.string.optional),
    onClearError = { onClearInputError(WishlistItemForm.Input.Store) }
  )

  val priceState = rememberTextInputState(
    initialValue = uiState.form.unitPrice.takeIf { it != 0f }?.toString().orEmpty(),
    onClearError = { onClearInputError(WishlistItemForm.Input.Price) }
  )

  val amountState = rememberTextInputState(
    initialValue = uiState.form.amount.toString(),
    onClearError = { onClearInputError(WishlistItemForm.Input.Amount) }
  )

  val linkState = rememberTextInputState(
    initialValue = uiState.form.link,
    supportingText = stringResource(R.string.optional),
    onClearError = { onClearInputError(WishlistItemForm.Input.Link) }
  )

  val tagsState = rememberTextInputState(
    initialValue = uiState.form.tags,
    supportingText = stringResource(R.string.wishlists_new_item_tags_input_support),
    onClearError = { onClearInputError(WishlistItemForm.Input.Tags) }
  )

  val descriptionState = rememberTextInputState(
    initialValue = uiState.form.description,
    supportingText = stringResource(R.string.wishlists_new_item_description_input_support),
    onClearError = { onClearInputError(WishlistItemForm.Input.Description) }
  )

  var prioritySelected: WishlistItem.Priority by remember(uiState.form.priority) {
    mutableStateOf(uiState.form.priority)
  }

  var imageSelected: ImagePicker.Resource? by remember(uiState.form.photo) {
    mutableStateOf(uiState.form.photo)
  }

  val isButtonEnabled by remember(uiState.form) {
    derivedStateOf {
      nameState.text.isNotBlank() &&
          priceState.text.isNotBlank() &&
          amountState.text.isNotBlank()
    }
  }

  LaunchedEffect(uiState.formErrors) {
    nameState.error(uiState.formErrors.name)
    storeState.error(uiState.formErrors.store)
    priceState.error(uiState.formErrors.unitPrice)
    amountState.error(uiState.formErrors.amount)
    linkState.error(uiState.formErrors.link)
    tagsState.error(uiState.formErrors.tags)
    descriptionState.error(uiState.formErrors.description)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.wishlists_new_item_title)) },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onCancel
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.cancel)
              )
            }
          }
        )
      },
    ) { paddings ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(paddings)
          .padding(
            horizontal = 16.dp,
            vertical = 24.dp
          ),
      ) {

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ImagePicker(
              modifier = Modifier
                .width(135.dp)
                .height(122.dp),
              initial = uiState.form.photo,
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

          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            TextInput(
              modifier = Modifier.fillMaxWidth(),
              state = nameState,
              label = stringResource(R.string.wishlists_new_item_name_input),
              leadingIcon = painterResource(R.drawable.ic_gift),
              singleLine = true
            )

            TextInput(
              modifier = Modifier.fillMaxWidth(),
              state = storeState,
              label = stringResource(R.string.wishlists_new_item_store_input),
              leadingIcon = Icons.Outlined.Store,
              singleLine = true,
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          TextInput(
            modifier = Modifier.weight(1f),
            state = priceState,
            label = stringResource(R.string.wishlists_new_item_price_input),
            leadingIcon = Icons.Outlined.EuroSymbol,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
          )

          TextInput(
            modifier = Modifier.weight(1f),
            state = amountState,
            label = stringResource(R.string.wishlists_new_item_amount_input),
            leadingIcon = Icons.Outlined.LocalGroceryStore,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DropdownInput(
          modifier = Modifier.fillMaxWidth(),
          items = WishlistItem.Priority.entries.mapIndexed { index, priority ->
            DropdownInput.Option(
              id = index,
              text = priority.name(),
              trailingIcon = rememberVectorPainter(priority.icon()),
              trailingIconColor = priority.color()
            )
          },
          label = stringResource(R.string.wishlists_new_item_priority_input),
          leadingIcon = prioritySelected.icon(),
          onSelectionChanged = { index ->
            if (index != null) {
              prioritySelected = WishlistItem.Priority.entries[index]
            }
          },
          allowUnselect = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          TextInput(
            modifier = Modifier.weight(1f),
            state = linkState,
            label = stringResource(R.string.wishlists_new_item_link_input),
            leadingIcon = Icons.Outlined.Link,
            singleLine = true,
          )

          AnimatedVisibility(visible = linkState.text.isNotBlank()) {
            FilledTonalButton(
              shapes = ButtonShape,
              onClick = { onAutocompleteByLink(linkState.text) }
            ) {
              Icon(
                painter = painterResource(R.drawable.ic_autocomplete),
                contentDescription = null
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = tagsState,
          label = stringResource(R.string.wishlists_new_item_tags_input),
          leadingIcon = Icons.Outlined.Tag,
          singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = descriptionState,
          label = stringResource(R.string.wishlists_new_item_description_input),
          leadingIcon = Icons.AutoMirrored.Rounded.Notes,
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = {
            val form = WishlistItemForm(
              photo = imageSelected,
              name = nameState.text,
              description = descriptionState.text,
              store = storeState.text,
              unitPrice = priceState.text.toFloatOrNull() ?: Float.NaN,
              amount = amountState.text.toIntOrNull() ?: 1,
              priority = prioritySelected,
              link = linkState.text,
              tags = tagsState.text,
            )
            onCreate(form)
          }
        ) {
          ButtonText(text = stringResource(R.string.create))
        }
      }
    }

    uiState.error?.let { error ->
      ErrorDialog(
        uiModel = error,
        onDismiss = onDismissError,
      )
    }

    if (uiState.isLoading) {
      Loader(modifier = Modifier.fillMaxSize())
    }
  }
}

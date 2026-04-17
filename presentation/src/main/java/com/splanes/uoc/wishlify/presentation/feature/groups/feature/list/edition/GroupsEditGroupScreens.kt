package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.edition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.components.GroupMemberPicker
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupForm
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GroupsEditGroupFormScreen(
  uiState: GroupsEditGroupUiState.Form,
  onEdit: (form: GroupsNewGroupForm) -> Unit,
  onSaveCurrentForm: (form: GroupsNewGroupForm) -> Unit,
  onSearchUsers: () -> Unit,
  onClearInputError: (GroupsNewGroupForm.Input) -> Unit,
  onDismissError: () -> Unit,
  onCancel: () -> Unit,
) {

  var imageSelected: ImagePicker.Resource? by remember { mutableStateOf(null) }
  val nameState = rememberTextInputState(
    initialValue = uiState.form.name,
    onClearError = { onClearInputError(GroupsNewGroupForm.Input.Name) }
  )

  val isButtonEnabled by remember(uiState.form) {
    derivedStateOf {
      nameState.text.isNotBlank() && uiState.form.members.isNotEmpty()
    }
  }

  LaunchedEffect(uiState.form) {
    nameState.onValueChanged(uiState.form.name)
    imageSelected = uiState.form.photo
  }

  LaunchedEffect(uiState.formErrors) {
    nameState.error(uiState.formErrors.nameError)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(text = stringResource(R.string.groups_edit_group))
              Text(
                text = uiState.group.name,
                style = WishlifyTheme.typography.bodySmall
              )
            }
          },
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
        Column(
          modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ImagePicker(
            modifier = Modifier
              .width(135.dp)
              .height(122.dp),
            initial = imageSelected,
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

        Spacer(Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = nameState,
          label = stringResource(R.string.groups_new_group_name_input_label),
          leadingIcon = Icons.Outlined.Group,
          singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        GroupMemberPicker(
          modifier = Modifier.fillMaxWidth(),
          users = uiState.form.members,
          onRemoveUser = { user ->
            val form = GroupsNewGroupForm(
              photo = imageSelected,
              name = nameState.text,
              members = uiState.form.members - user
            )
            onSaveCurrentForm(form)
          },
          onSearchUser = {
            val form = GroupsNewGroupForm(
              photo = imageSelected,
              name = nameState.text,
              members = uiState.form.members
            )
            onSaveCurrentForm(form)
            onSearchUsers()
          }
        )

        AnimatedVisibility(
          visible = uiState.formErrors.membersError != null,
          enter = expandVertically(),
          exit = shrinkVertically()
        ) {
          Text(
            text = uiState.formErrors.membersError.orEmpty(),
            style = WishlifyTheme.typography.labelMedium,
            color = WishlifyTheme.colorScheme.error,
          )
        }

        Spacer(Modifier.weight(1f))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = {
            val form = GroupsNewGroupForm(
              photo = imageSelected,
              name = nameState.text,
              members = uiState.form.members
            )
            onEdit(form)
          }
        ) {
          ButtonText(text = stringResource(R.string.edit))
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

@Composable
fun GroupsEditGroupErrorScreen(
  uiState: GroupsEditGroupUiState.Error,
  onCancel: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(text = stringResource(R.string.groups_edit_group))
              Text(
                text = uiState.groupName,
                style = WishlifyTheme.typography.bodySmall
              )
            }
          },
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
        Spacer(Modifier.weight(.5f))

        // Used as error component as well
        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.generic_error),
          title = stringResource(R.string.wishlists_detail_error_title),
          description = stringResource(R.string.wishlists_detail_error_description)
        )

        Spacer(Modifier.weight(1f))
      }
    }
  }
}

@Composable
fun GroupsEditGroupLoadingScreen(
  uiState: GroupsEditGroupUiState.Loading,
  onCancel: () -> Unit
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(text = stringResource(R.string.groups_edit_group))
              Text(
                text = uiState.groupName,
                style = WishlifyTheme.typography.bodySmall
              )
            }
          },
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
        Spacer(Modifier.weight(.5f))

        Loader(
          modifier = Modifier.fillMaxWidth(),
          containerColor = Color.Transparent
        )

        Spacer(Modifier.weight(1f))
      }
    }
  }
}
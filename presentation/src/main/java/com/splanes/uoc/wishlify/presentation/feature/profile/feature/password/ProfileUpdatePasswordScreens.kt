@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Password
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.input.password.PasswordInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordForm
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun ProfileUpdatePasswordScreen(
  uiState: ProfileUpdatePasswordUiState.Form,
  onUpdate: (form: UserProfileUpdatePasswordForm) -> Unit,
  onClearInputError: (UserProfileUpdatePasswordForm.Input) -> Unit,
  onBack: () -> Unit,
  onDismissError: () -> Unit,
) {

  val currentPasswordState = rememberTextInputState(
    onClearError = { onClearInputError(UserProfileUpdatePasswordForm.Input.CurrentPassword) }
  )
  val newPasswordState = rememberTextInputState(
    onClearError = { onClearInputError(UserProfileUpdatePasswordForm.Input.NewPassword) }
  )
  val newPasswordConfirmState = rememberTextInputState(
    onClearError = { onClearInputError(UserProfileUpdatePasswordForm.Input.NewPasswordConfirm) }
  )

  val isButtonEnabled by remember(uiState.user) {
    derivedStateOf {
      currentPasswordState.text.isNotBlank() &&
          newPasswordState.text.isNotBlank() &&
          newPasswordConfirmState.text.isNotBlank()
    }
  }

  LaunchedEffect(uiState.formErrors) {
    currentPasswordState.error(uiState.formErrors.currentPassword)
    newPasswordState.error(uiState.formErrors.newPassword)
    newPasswordConfirmState.error(uiState.formErrors.newPasswordConfirm)
  }

  Box(Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.profile_update_password_title)) },
          navigationIcon = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onBack
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back)
              )
            }
          },
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
        Text(
          text = htmlString(R.string.profile_update_password_description),
          style = WishlifyTheme.typography.bodyLarge,
          color = WishlifyTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(16.dp))

        PasswordInput(
          modifier = Modifier.fillMaxWidth(),
          state = currentPasswordState,
          label = stringResource(R.string.profile_update_password_input_label),
          leadingIcon = Icons.Rounded.Password,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(16.dp))

        PasswordInput(
          modifier = Modifier.fillMaxWidth(),
          state = newPasswordState,
          label = stringResource(R.string.profile_update_password_new_input_label),
          leadingIcon = Icons.Rounded.Password,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(16.dp))

        PasswordInput(
          modifier = Modifier.fillMaxWidth(),
          state = newPasswordConfirmState,
          label = stringResource(R.string.profile_update_password_new_confirm_input_label),
          leadingIcon = Icons.Rounded.Password,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(16.dp))
        Spacer(Modifier.weight(1f))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = {
            val form = UserProfileUpdatePasswordForm(
              currentPassword = currentPasswordState.text,
              newPassword = newPasswordState.text,
              newPasswordConfirm = newPasswordConfirmState.text,
            )
            onUpdate(form)
          }
        ) {
          ButtonText(text = stringResource(R.string.update))
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
fun ProfileUpdatePasswordLoadingScreen(
  onBack: () -> Unit
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.profile_update_password_title)) },
        navigationIcon = {
          IconButton(
            shapes = IconButtonShape,
            onClick = onBack
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
        },
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
      verticalArrangement = Arrangement.Center
    ) {

      Loader(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent
      )
    }
  }
}

@Composable
fun ProfileUpdatePasswordErrorScreen(
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.profile_update_password_title)) },
        navigationIcon = {
          IconButton(
            shapes = IconButtonShape,
            onClick = onBack
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
        },
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
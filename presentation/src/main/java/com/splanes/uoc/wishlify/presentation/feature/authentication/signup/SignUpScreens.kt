package com.splanes.uoc.wishlify.presentation.feature.authentication.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.OrDivider
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.input.password.PasswordInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.SignUpForm
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignUpFormScreen(
  uiState: SignUpUiState.SignUpForm,
  onDismissError: () -> Unit,
  onClearInputError: (input: SignUpForm.Input) -> Unit,
  onSignUp: (form: SignUpForm) -> Unit,
  onGoogleSignUp: () -> Unit,
  onSignIn: () -> Unit,
) {

  val focusManager = LocalFocusManager.current
  val emailFocusRequester = remember { FocusRequester() }
  val passwordFocusRequester = remember { FocusRequester() }
  val usernameState = rememberTextInputState(
    onClearError = { onClearInputError(SignUpForm.Input.Username) }
  )
  val emailState = rememberTextInputState(
    onClearError = { onClearInputError(SignUpForm.Input.Email) }
  )
  val passwordState = rememberTextInputState(
    onClearError = { onClearInputError(SignUpForm.Input.Password) }
  )
  val isAccessButtonEnabled by remember {
    derivedStateOf {
      emailState.text.isNotBlank() &&
          usernameState.text.isNotBlank() &&
          passwordState.text.isNotBlank()
    }
  }

  LaunchedEffect(uiState.usernameInputError) {
    usernameState.error(uiState.usernameInputError)
  }

  LaunchedEffect(uiState.emailInputError) {
    emailState.error(uiState.emailInputError)
  }

  LaunchedEffect(uiState.passwordInputError) {
    passwordState.error(uiState.passwordInputError)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Box(
        modifier = Modifier.fillMaxWidth()
      ) {

        IconButton(
          modifier = Modifier.align(Alignment.CenterStart),
          shapes = IconButtonShape,
          onClick = onSignIn
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = null,
            tint = WishlifyTheme.colorScheme.onSurface,
          )
        }

        Text(
          modifier = Modifier.align(Alignment.Center),
          text = stringResource(R.string.app_name),
          style = WishlifyTheme.typography.decorationMedium,
          color = WishlifyTheme.colorScheme.primary
        )
      }


      Spacer(modifier = Modifier.height(50.dp))

      Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.auth_sign_up),
        style = WishlifyTheme.typography.displaySmall,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(34.dp))

      TextInput(
        modifier = Modifier.fillMaxWidth(),
        state = usernameState,
        leadingIcon = Icons.Outlined.AccountBox,
        label = stringResource(R.string.auth_username),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
          onNext = { emailFocusRequester.requestFocus() },
        ),
      )

      Spacer(modifier = Modifier.height(12.dp))

      TextInput(
        modifier = Modifier
          .fillMaxWidth()
          .focusRequester(emailFocusRequester),
        state = emailState,
        leadingIcon = Icons.Rounded.AlternateEmail,
        label = stringResource(R.string.auth_email),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
          onNext = { passwordFocusRequester.requestFocus() },
        ),
      )

      Spacer(modifier = Modifier.height(12.dp))

      PasswordInput(
        modifier = Modifier
          .fillMaxWidth()
          .focusRequester(passwordFocusRequester),
        state = passwordState,
        label = stringResource(R.string.auth_password),
        leadingIcon = Icons.Rounded.Password,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Password,
          imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
          onDone = {
            passwordFocusRequester.freeFocus()
            focusManager.clearFocus()
          },
        ),
      )

      Spacer(modifier = Modifier.height(34.dp))

      Button(
        modifier = Modifier.fillMaxWidth(),
        shapes = ButtonShape,
        enabled = isAccessButtonEnabled,
        onClick = {
          val form = SignUpForm(
            email = emailState.text,
            username = usernameState.text,
            password = passwordState.text
          )
          onSignUp(form)
        },
      ) {
        ButtonText(text = stringResource(R.string.auth_sign_up))
      }

      OrDivider(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp),
        text = stringResource(R.string.or)
      )

      OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        shapes = ButtonShape,
        onClick = onGoogleSignUp
      ) {
        Image(
          modifier = Modifier.size(24.dp),
          painter = painterResource(R.drawable.google),
          contentDescription = "Google"
        )

        Spacer(modifier = Modifier.width(16.dp))

        ButtonText(text = stringResource(R.string.auth_google_account))
      }

      Spacer(modifier = Modifier.weight(1f))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = stringResource(R.string.auth_already_account),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onSurface
        )

        TextButton(
          shapes = ButtonShape,
          onClick = onSignIn,
        ) {
          Text(
            text = stringResource(R.string.auth_log_in),
            style = WishlifyTheme.typography.bodyMedium,
            color = WishlifyTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.width(24.dp))
    }

    if (uiState.isLoading) {
      Loader(modifier = Modifier.fillMaxSize())
    }

    uiState.error?.let { error ->
      ErrorDialog(
        uiModel = error,
        onDismiss = onDismissError,
      )
    }
  }

}
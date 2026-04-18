package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.components.ProfileUpdateEmailBanner
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateForm
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileUpdateScreen(
  uiState: ProfileUpdateUiState.Form,
  onUpdate: (form: UserProfileUpdateForm) -> Unit,
  onClearInputError: (UserProfileUpdateForm.Input) -> Unit,
  onDismissError: () -> Unit,
  onBack: () -> Unit
) {

  var imageSelected: ImagePicker.Resource? by remember { mutableStateOf(uiState.form.photo) }
  val usernameState = rememberTextInputState(
    initialValue = uiState.form.username,
    onClearError = { onClearInputError(UserProfileUpdateForm.Input.Username) }
  )
  val emailState = rememberTextInputState(
    initialValue = uiState.form.email,
    onClearError = { onClearInputError(UserProfileUpdateForm.Input.Email) }
  )

  val isButtonEnabled by remember(uiState.user) {
    derivedStateOf {
      uiState.user.username != usernameState.text
          || uiState.user.email != emailState.text
          || uiState.form.photo != imageSelected
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.profile_update_profile)) },
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
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        Column(
          modifier = Modifier.width(135.dp)
        ) {
          ImagePicker(
            modifier = Modifier
              .fillMaxWidth()
              .height(122.dp),
            initial = imageSelected,
            preset = emptyList(),
            onSelectionChanged = { image -> imageSelected = image }
          )

          Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.profile_avatar_input_supporting_text),
            style = WishlifyTheme.typography.labelSmall,
            color = WishlifyTheme.colorScheme.outline,
            textAlign = TextAlign.Center
          )
        }


        Spacer(Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = usernameState,
          leadingIcon = Icons.Outlined.AccountBox,
          label = stringResource(R.string.auth_username),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        Spacer(Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = emailState,
          leadingIcon = Icons.Rounded.AlternateEmail,
          label = stringResource(R.string.auth_email),
          enabled = !uiState.form.isSocialAccount,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(16.dp))

        ProfileUpdateEmailBanner(
          modifier = Modifier.fillMaxWidth(),
          description = if (uiState.form.isSocialAccount) {
            stringResource(R.string.profile_update_email_banner_social_description)
          } else {
            stringResource(R.string.profile_update_email_banner_description)
          }
        )

        Spacer(Modifier.weight(1f))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = {
            val form = UserProfileUpdateForm(
              photo = imageSelected,
              username = usernameState.text,
              email = emailState.text,
              isSocialAccount = uiState.form.isSocialAccount
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
fun ProfileUpdateLoadingScreen(
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.profile_update_profile)) },
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
fun ProfileUpdateErrorScreen(
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.profile_update_profile)) },
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
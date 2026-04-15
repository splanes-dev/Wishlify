package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.edition

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components.SecretSantaNewEventBasicsForm
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components.SecretSantaNewEventExclusionsForm
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components.SecretSantaNewEventParticipantsForm
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components.SecretSantaNewEventStepper
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventForm
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaUpdateEventBasicsScreen(
  uiState: SecretSantaUpdateEventUiState.Event,
  onNext: (SecretSantaNewEventForm) -> Unit,
  onClearInputError: (SecretSantaNewEventForm.Input) -> Unit,
  onDismissError: () -> Unit,
  onCancel: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.secret_santa_update_event_title)) },
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

        SecretSantaNewEventStepper(uiState.step)

        SecretSantaNewEventBasicsForm(
          modifier = Modifier
            .weight(1f)
            .padding(vertical = 16.dp),
          form = uiState.form,
          errors = uiState.formErrors,
          onClearInputError = onClearInputError,
          onNext = onNext
        )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaUpdateEventParticipantsScreen(
  uiState: SecretSantaUpdateEventUiState.Event,
  onCreateGroup: () -> Unit,
  onSearchUsers: () -> Unit,
  onSkipAndCreate: (form: SecretSantaNewEventForm) -> Unit,
  onNext: (form: SecretSantaNewEventForm) -> Unit,
  onBack: () -> Unit,
  onDismissError: () -> Unit,
  onCancel: () -> Unit,
) {
  BackHandler {
    onBack()
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.secret_santa_update_event_title)) },
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

        SecretSantaNewEventStepper(uiState.step)

        Text(
          modifier = Modifier.padding(top = 16.dp),
          text = htmlString(R.string.secret_santa_new_event_participants_description),
          style = WishlifyTheme.typography.bodyLarge,
          color = WishlifyTheme.colorScheme.onSurface,
          textAlign = TextAlign.Justify
        )

        SecretSantaNewEventParticipantsForm(
          modifier = Modifier
            .weight(1f)
            .padding(vertical = 16.dp),
          form = uiState.form,
          groups = uiState.groups,
          inviteLink = uiState.inviteLink,
          isBannerVisible = false,
          onCreateGroup = onCreateGroup,
          onSearchUsers = onSearchUsers,
          onSkipAndCreate = onSkipAndCreate,
          onNext = onNext
        )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaUpdateEventExclusionsScreen(
  uiState: SecretSantaUpdateEventUiState.Event,
  onCreate: (form: SecretSantaNewEventForm) -> Unit,
  onClearInputError: (SecretSantaNewEventForm.Input) -> Unit,
  onBack: () -> Unit,
  onCancel: () -> Unit,
  onDismissError: () -> Unit,
) {
  val snackbarState = remember { SnackbarHostState() }

  BackHandler {
    onBack()
  }

  LaunchedEffect(uiState.formErrors) {
    val error = uiState.formErrors.exclusions
    if (error != null) {
      snackbarState.showSnackbar(
        message = error,
        withDismissAction = true,
        duration = SnackbarDuration.Indefinite
      )
      onClearInputError(SecretSantaNewEventForm.Input.Exclusions)
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.secret_santa_update_event_title)) },
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
      snackbarHost = {
        SnackbarHost(snackbarState) { data ->
          Snackbar(
            snackbarData = data,
            shape = WishlifyTheme.shapes.small,
            containerColor = WishlifyTheme.colorScheme.errorContainer,
            contentColor = WishlifyTheme.colorScheme.onErrorContainer,
            dismissActionContentColor = WishlifyTheme.colorScheme.onErrorContainer,
          )
        }
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

        SecretSantaNewEventStepper(uiState.step)

        Text(
          modifier = Modifier.padding(top = 16.dp),
          text = stringResource(R.string.secret_santa_new_event_exclusions_description),
          style = WishlifyTheme.typography.bodyLarge,
          color = WishlifyTheme.colorScheme.onSurface,
          textAlign = TextAlign.Justify
        )

        SecretSantaNewEventExclusionsForm(
          modifier = Modifier
            .weight(1f)
            .padding(vertical = 16.dp),
          form = uiState.form,
          participants = uiState.allParticipants,
          createButtonText = stringResource(R.string.edit),
          onCreate = onCreate
        )
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaUpdateEventLoadingScreen(
  onCancel: () -> Unit
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.secret_santa_update_event_title)) },
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
      verticalArrangement = Arrangement.Center
    ) {

      Loader(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaUpdateEventErrorScreen(
  onCancel: () -> Unit
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.secret_santa_update_event_title)) },
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
      verticalArrangement = Arrangement.Center
    ) {

      // Used as error component as well
      EmptyState(
        modifier = Modifier.fillMaxWidth(),
        image = painterResource(R.drawable.generic_error),
        title = stringResource(R.string.wishlists_detail_error_title),
        description = stringResource(R.string.wishlists_detail_error_description)
      )
    }
  }
}
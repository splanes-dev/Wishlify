package com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.components.SecretSantaNotificationSection
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.components.SharedWishlistNotificationSection
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.model.UserProfileNotificationsForm

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileNotificationsScreen(
  uiState: ProfileNotificationsUiState.Notifications,
  onUpdate: (UserProfileNotificationsForm) -> Unit,
  onDismissError: () -> Unit,
  onBack: () -> Unit
) {

  var form by remember(uiState.form) { mutableStateOf(uiState.form) }
  val isButtonEnabled by remember(uiState.form) {
    derivedStateOf {
      uiState.form.sharedWishlistChat != form.sharedWishlistChat ||
      uiState.form.sharedWishlistUpdates != form.sharedWishlistUpdates ||
      uiState.form.sharedWishlistsDeadlineReminders != form.sharedWishlistsDeadlineReminders ||
      uiState.form.secretSantaChat != form.secretSantaChat ||
      uiState.form.secretSantaDeadlineReminders != form.secretSantaDeadlineReminders
    }
  }

  Box(Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.profile_notifications_title)) },
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

        SharedWishlistNotificationSection(
          modifier = Modifier.fillMaxWidth(),
          form = form,
          onChange = { form = it }
        )

        Spacer(Modifier.height(16.dp))

        SecretSantaNotificationSection(
          modifier = Modifier.fillMaxWidth(),
          form = form,
          onChange = { form = it }
        )

        Spacer(Modifier.weight(1f))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = { onUpdate(form) }
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
fun ProfileNotificationsLoadingScreen(
  onBack: () -> Unit
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.profile_notifications_title)) },
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
fun ProfileNotificationsErrorScreen(
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.profile_notifications_title)) },
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
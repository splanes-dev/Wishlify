package com.splanes.uoc.wishlify.presentation.feature.profile.feature.main

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.main.components.ProfileHeader
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.main.components.ProfileOptions
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.main.model.ProfileOption
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileMainScreen(
  uiState: ProfileMainUiState.Profile,
  onSignOut: () -> Unit,
  onUpdateProfile: () -> Unit,
  onChangePassword: () -> Unit,
  onAdminNotifications: () -> Unit,
  onAdminStore: () -> Unit,
  onAdminHobbies: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.profile)) },
        )
      }
    ) { paddings ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddings)
          .padding(
            horizontal = 16.dp,
            vertical = 24.dp
          ),
      ) {
        ProfileHeader(
          modifier = Modifier.fillMaxWidth(),
          user = uiState.user
        )

        Spacer(Modifier.height(32.dp))

        ProfileOptions(
          modifier = Modifier.fillMaxWidth(),
          options = buildList {
            add(ProfileOption.UpdateProfile)
            if (!uiState.user.isSocialAccount) add(ProfileOption.ChangePassword)
            add(ProfileOption.AdminNotifications)
            add(ProfileOption.Store)
            add(ProfileOption.Hobbies)
          },
          onOptionClick = { option ->
            when (option) {
              ProfileOption.UpdateProfile -> onUpdateProfile()
              ProfileOption.ChangePassword -> onChangePassword()
              ProfileOption.AdminNotifications -> onAdminNotifications()
              ProfileOption.Store -> onAdminStore()
              ProfileOption.Hobbies -> onAdminHobbies()
            }
          }
        )

        Spacer(Modifier.weight(1f))

        Button(
          modifier = Modifier
            .fillMaxWidth(.7f)
            .align(Alignment.CenterHorizontally),
          shapes = ButtonShape,
          colors = ButtonDefaults.buttonColors(
            containerColor = WishlifyTheme.colorScheme.errorContainer,
            contentColor = WishlifyTheme.colorScheme.onErrorContainer
          ),
          onClick = onSignOut
        ) {
          ButtonText(text = stringResource(R.string.profile_close_session))
        }
      }
    }
  }
}

@Composable
fun ProfileMainLoadingScreen() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.profile)) },
        )
      }
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
          modifier = Modifier.weight(1f),
          containerColor = Color.Transparent
        )
      }
    }
  }
}

@Composable
fun ProfileMainErrorScreen() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.profile)) },
        )
      }
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
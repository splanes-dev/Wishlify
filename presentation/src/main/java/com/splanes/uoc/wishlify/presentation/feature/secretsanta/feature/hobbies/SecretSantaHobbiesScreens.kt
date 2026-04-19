package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.SmokeFeatureDialog
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.components.SecretSantaAISuggestionsSection
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.components.SecretSantaHobbiesSection

@Composable
fun SecretSantaHobbiesScreen(
  uiState: SecretSantaHobbiesUiState.Hobbies,
  onCancel: () -> Unit,
) {

  var isDialogVisible by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            Text(text = stringResource(R.string.secret_santa_hobbies_title))
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
          .padding(16.dp),
      ) {

        SecretSantaHobbiesSection(
          modifier = Modifier.fillMaxWidth(),
          user = uiState.user,
        )

        SecretSantaAISuggestionsSection(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
          onGenerateSuggestions = { isDialogVisible = true }
        )
      }
    }

    if (isDialogVisible) {
      SmokeFeatureDialog(
        onDismiss = { isDialogVisible = false },
        onAnswer = { interested -> /* TODO: collect data */ }
      )
    }
  }
}

@Composable
fun SecretSantaHobbiesLoadingScreen(onCancel: () -> Unit) {
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            Text(text = stringResource(R.string.secret_santa_hobbies_title))
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
        verticalArrangement = Arrangement.Center
      ) {

        Loader(
          modifier = Modifier.fillMaxWidth(),
          containerColor = Color.Transparent
        )
      }
    }
  }
}

@Composable
fun SecretSantaHobbiesErrorScreen(onCancel: () -> Unit) {
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            Text(text = stringResource(R.string.secret_santa_hobbies_title))
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
}
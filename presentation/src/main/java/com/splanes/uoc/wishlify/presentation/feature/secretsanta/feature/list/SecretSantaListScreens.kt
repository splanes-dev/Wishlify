package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components.SecretSantaEventCard
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components.SecretSantaEventsFinishedHeader
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components.SecretSantaEventsSettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components.SecretSantaSearchBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.model.SecretSantaEventsSettings
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaListScreen(
  uiState: SecretSantaListUiState.Events,
  onNewEvent: () -> Unit,
  onEventClick: (SecretSantaEvent) -> Unit,
  onDismissError: () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  var isSettingsModalOpen by remember { mutableStateOf(false) }
  val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isSearchModalOpen by remember { mutableStateOf(false) }
  val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val existsEventsFinished by remember(uiState.events) {
    derivedStateOf { uiState.events.any { e -> e.isFinished() } }
  }
  var areEventsFinishedVisible by remember { mutableStateOf(true) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.secret_santa)) },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = { isSettingsModalOpen = true }
            ) {
              Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = stringResource(R.string.settings)
              )
            }
          }
        )
      },
      floatingActionButton = {
        FloatingActionButton(
          shape = WishlifyTheme.shapes.medium,
          containerColor = WishlifyTheme.colorScheme.tertiaryContainer,
          contentColor = WishlifyTheme.colorScheme.onTertiaryContainer,
          onClick = onNewEvent,
        ) {
          Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
          )
        }
      },
      floatingActionButtonPosition = FabPosition.End
    ) { paddings ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddings),
        contentPadding = PaddingValues(
          vertical = 24.dp,
          horizontal = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        if (uiState.events.filter { !it.isFinished() }.isNotEmpty()) {
          items(
            items = uiState.events.filter { !it.isFinished() },
            key = { event -> event.id }
          ) { event ->
            SecretSantaEventCard(
              modifier = Modifier
                .fillMaxWidth()
                .animateItem(),
              event = event,
              onClick = { onEventClick(event) }
            )
          }
        } else {
          item {
            EmptyState(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
              image = painterResource(R.drawable.img_secret_santa_empty),
              title = stringResource(R.string.secret_santa_list_empty_state_title),
              description = stringResource(R.string.secret_santa_list_empty_state_active_description),
              descriptionStyle = WishlifyTheme.typography.titleMedium
            )
          }
        }

        if (existsEventsFinished) {
          item(key = 1, contentType = "header") {
            SecretSantaEventsFinishedHeader(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
              isVisible = areEventsFinishedVisible,
              onChangeVisibility = { areEventsFinishedVisible = !areEventsFinishedVisible }
            )
          }

          if (areEventsFinishedVisible) {
            items(
              items = uiState.events.filter { it.isFinished() },
              key = { item -> item.id }
            ) { event ->
              SecretSantaEventCard(
                modifier = Modifier
                  .fillMaxWidth()
                  .animateItem(),
                event = event,
                onClick = { onEventClick(event) }
              )
            }
          }
        }
      }
    }

    SecretSantaEventsSettingsBottomSheet(
      visible = isSettingsModalOpen,
      sheetState = settingsSheetState,
      onDismiss = { isSettingsModalOpen = false },
      onSettingClick = { setting ->
        when (setting) {
          SecretSantaEventsSettings.Search -> isSearchModalOpen = true
          SecretSantaEventsSettings.Filter -> {
            // TODO
          }
        }
        coroutineScope
          .launch { settingsSheetState.hide() }
          .invokeOnCompletion { isSettingsModalOpen = false }
      }
    )

    SecretSantaSearchBottomSheet(
      visible = isSearchModalOpen,
      sheetState = searchSheetState,
      events = uiState.events,
      onDismiss = { isSearchModalOpen = false },
      onClick = { event ->
        onEventClick(event)
        coroutineScope
          .launch { searchSheetState.hide() }
          .invokeOnCompletion { isSearchModalOpen = false }
      },
    )

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
fun SecretSantaListEmptyScreen(
  uiState: SecretSantaListUiState.Empty,
  onNewEvent: () -> Unit,
  onDismissError: () -> Unit,
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
          title = { Text(text = stringResource(R.string.secret_santa)) },
        )
      },
      floatingActionButton = {
        FloatingActionButton(
          shape = WishlifyTheme.shapes.medium,
          containerColor = WishlifyTheme.colorScheme.tertiaryContainer,
          contentColor = WishlifyTheme.colorScheme.onTertiaryContainer,
          onClick = onNewEvent,
        ) {
          Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
          )
        }
      },
      floatingActionButtonPosition = FabPosition.End
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

        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.img_secret_santa_empty),
          title = stringResource(R.string.secret_santa_list_empty_state_title),
          description = stringResource(R.string.secret_santa_list_empty_state_description)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretSantaListLoadingScreen() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.secret_santa)) },
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
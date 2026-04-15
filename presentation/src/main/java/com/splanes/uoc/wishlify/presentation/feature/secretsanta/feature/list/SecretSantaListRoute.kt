package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEvent

@Composable
fun SecretSantaListRoute(
  viewModel: SecretSantaListViewModel,
  onNavToNewEvent: () -> Unit,
  onNavToDetail: (SecretSantaEvent) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is SecretSantaListUiState.Empty ->
      SecretSantaListEmptyScreen(
        uiState = state,
        onNewEvent = onNavToNewEvent,
        onDismissError = viewModel::onDismissError,
      )

    is SecretSantaListUiState.Events ->
      SecretSantaListScreen(
        uiState = state,
        onNewEvent = onNavToNewEvent,
        onEventClick = onNavToDetail,
        onDismissError = viewModel::onDismissError,
      )

    SecretSantaListUiState.Loading ->
      SecretSantaListLoadingScreen()
  }
}
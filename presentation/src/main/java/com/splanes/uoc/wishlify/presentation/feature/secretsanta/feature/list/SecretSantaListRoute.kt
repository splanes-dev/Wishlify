package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation.SecretSantaExternalAction
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation.SecretSantaExternalActionHandler

@Composable
fun SecretSantaListRoute(
  viewModel: SecretSantaListViewModel,
  externalActionHandler: SecretSantaExternalActionHandler,
  onNavToNewEvent: () -> Unit,
  onNavToDetail: (SecretSantaEvent) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(externalActionHandler) {
    externalActionHandler.consume { action ->
      when (action) {
        is SecretSantaExternalAction.JoinToParticipantsByToken -> {
          viewModel.onJoinToParticipantsByToken(action.token)
          externalActionHandler.clean()
        }

        is SecretSantaExternalAction.OpenChatById -> {
          val secretSantaEvent = viewModel.fetchSecretSantaEventById(action.secretSantaId)
          onNavToDetail(secretSantaEvent)
        }
      }
    }
  }

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
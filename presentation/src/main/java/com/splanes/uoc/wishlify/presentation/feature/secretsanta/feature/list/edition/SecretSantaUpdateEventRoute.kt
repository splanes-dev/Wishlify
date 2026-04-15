package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.edition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventStep

@Composable
fun SecretSantaUpdateEventRoute(
  viewModel: SecretSantaUpdateEventViewModel,
  onNavToCreateGroup: () -> Unit,
  onNavToSearchUsers: () -> Unit,
  onFinish: (updated: Boolean) -> Unit
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        SecretSantaUpdateEventUiSideEffect.EventUpdated -> onFinish(true)
      }
    }
  }

  when (val state = uiState) {
    SecretSantaUpdateEventUiState.Error ->
      SecretSantaUpdateEventErrorScreen(
        onCancel = { onFinish(false) }
      )

    SecretSantaUpdateEventUiState.Loading ->
      SecretSantaUpdateEventLoadingScreen(
        onCancel = { onFinish(false) }
      )

    is SecretSantaUpdateEventUiState.Event ->
      when (state.step) {
        SecretSantaNewEventStep.Basics ->
          SecretSantaUpdateEventBasicsScreen(
            uiState = state,
            onCancel = { onFinish(false) },
            onClearInputError = viewModel::onClearInputError,
            onDismissError = viewModel::onDismissError,
            onNext = viewModel::onNext
          )

        SecretSantaNewEventStep.Participants ->
          SecretSantaUpdateEventParticipantsScreen(
            uiState = state,
            onBack = viewModel::onPrevStep,
            onCancel = { onFinish(false) },
            onDismissError = viewModel::onDismissError,
            onNext = viewModel::onNext,
            onSkipAndCreate = viewModel::onCreate,
            onCreateGroup = onNavToCreateGroup,
            onSearchUsers = onNavToSearchUsers
          )

        SecretSantaNewEventStep.Exclusions ->
          SecretSantaUpdateEventExclusionsScreen(
            uiState = state,
            onCreate = viewModel::onNext,
            onClearInputError = viewModel::onClearInputError,
            onBack = viewModel::onPrevStep,
            onDismissError = viewModel::onDismissError,
            onCancel = { onFinish(false) }
          )
      }
  }
}
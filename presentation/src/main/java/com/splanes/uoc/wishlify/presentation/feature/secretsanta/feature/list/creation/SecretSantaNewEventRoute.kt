package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventStep

@Composable
fun SecretSantaNewEventRoute(
  viewModel: SecretSantaNewEventViewModel,
  onNavToCreateGroup: () -> Unit,
  onNavToSearchUsers: () -> Unit,
  onFinish: (created: Boolean) -> Unit
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        SecretSantaNewEventUiSideEffect.EventCreated -> onFinish(true)
      }
    }
  }

  when (uiState.step) {
    SecretSantaNewEventStep.Basics ->
      SecretSantaNewEventBasicsScreen(
        uiState = uiState,
        onCancel = { onFinish(false) },
        onClearInputError = viewModel::onClearInputError,
        onDismissError = viewModel::onDismissError,
        onNext = viewModel::onNext
      )

    SecretSantaNewEventStep.Participants ->
      SecretSantaNewEventParticipantsScreen(
        uiState = uiState,
        onBack = viewModel::onPrevStep,
        onCancel = { onFinish(false) },
        onDismissError = viewModel::onDismissError,
        onNext = viewModel::onNext,
        onSkipAndCreate = viewModel::onCreate,
        onCreateGroup = onNavToCreateGroup,
        onSearchUsers = onNavToSearchUsers
      )

    SecretSantaNewEventStep.Exclusions ->
      SecretSantaNewEventExclusionsScreen(
        uiState = uiState,
        onCreate = viewModel::onNext,
        onClearInputError = viewModel::onClearInputError,
        onBack = viewModel::onPrevStep,
        onDismissError = viewModel::onDismissError,
        onCancel = { onFinish(false) }
      )

  }
}
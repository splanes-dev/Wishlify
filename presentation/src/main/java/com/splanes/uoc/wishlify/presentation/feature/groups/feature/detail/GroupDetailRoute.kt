package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GroupDetailRoute(
  viewModel: GroupDetailViewModel,
  onNavToEdit: (groupId: String, name: String) -> Unit,
  onFinish: (result: Boolean) -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        GroupDetailUiSideEffect.GroupUpdated -> onFinish(true)
      }
    }
  }

  when (val state = uiState) {
    is GroupDetailUiState.Detail ->
      GroupDetailScreen(
        uiState = state,
        onEditGroup = { group -> onNavToEdit(group.id, group.name) },
        onLeaveGroup = viewModel::onLeaveGroup,
        onDismissError = viewModel::onDismissError,
        onBack = { onFinish(false) }
      )

    is GroupDetailUiState.Error ->
      GroupDetailErrorScreen(
        uiState = state,
        onBack = { onFinish(false) }
      )

    is GroupDetailUiState.Loading ->
      GroupDetailLoadingScreen(
        uiState = state,
        onBack = { onFinish(false) }
      )
  }
}
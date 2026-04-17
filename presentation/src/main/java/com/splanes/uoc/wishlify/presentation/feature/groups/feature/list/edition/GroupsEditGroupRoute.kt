package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.edition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GroupsEditGroupRoute(
  viewModel: GroupsEditGroupViewModel,
  onNavToSearchUsers: () -> Unit,
  onFinish: (result: Boolean) -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        GroupsEditGroupUiSideEffect.GroupUpdated -> onFinish(true)
      }
    }
  }

  when (val state = uiState) {
    is GroupsEditGroupUiState.Error ->
      GroupsEditGroupErrorScreen(
        uiState = state,
        onCancel = { onFinish(false) }
      )

    is GroupsEditGroupUiState.Form ->
      GroupsEditGroupFormScreen(
        uiState = state,
        onEdit = viewModel::onUpdateGroup,
        onSaveCurrentForm = viewModel::onSaveCurrentForm,
        onSearchUsers = onNavToSearchUsers,
        onClearInputError = viewModel::onClearInputError,
        onDismissError = viewModel::onDismissError,
        onCancel = { onFinish(false) }
      )

    is GroupsEditGroupUiState.Loading ->
      GroupsEditGroupLoadingScreen(
        uiState = state,
        onCancel = { onFinish(false) }
      )
  }
}
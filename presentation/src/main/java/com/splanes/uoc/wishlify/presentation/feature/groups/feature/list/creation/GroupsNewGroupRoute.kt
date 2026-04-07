package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GroupsNewGroupRoute(
  viewModel: GroupsNewGroupViewModel,
  onNavToSearchUsers: () -> Unit,
  onFinish: (result: Boolean) -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        GroupsNewGroupUiSideEffect.GroupCreated -> onFinish(true)
      }
    }
  }

  GroupsNewGroupScreen(
    uiState = uiState,
    onCreate = viewModel::onCreate,
    onSearchUsers = onNavToSearchUsers,
    onRemoveSelectedMember = viewModel::onRemoveSelectedMember,
    onCancel = { onFinish(false) },
    onClearInputError = viewModel::onClearInputError,
    onDismissError = viewModel::onDismissError,
  )
}
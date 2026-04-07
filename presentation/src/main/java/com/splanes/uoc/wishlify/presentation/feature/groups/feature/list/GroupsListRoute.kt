package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GroupsListRoute(
  viewModel: GroupsListViewModel,
  onNavToNewGroup: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is GroupsListUiState.Empty ->
      GroupsListEmptyScreen(
        uiState = state,
        onNewGroup = onNavToNewGroup,
        onDismissError = viewModel::onDismissError
      )

    is GroupsListUiState.Groups ->
      GroupsListScreen(
        uiState = state,
        onNewGroup = onNavToNewGroup,
        onDismissError = viewModel::onDismissError
      )

    GroupsListUiState.Loading ->
      GroupsListLoadingScreen()
  }
}
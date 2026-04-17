package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GroupsListRoute(
  viewModel: GroupsListViewModel,
  onNavToDetail: (groupId: String, name: String) -> Unit,
  onNavToEdit: (groupId: String, name: String) -> Unit,
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
        onGroupClick = { group -> onNavToDetail(group.id, group.name) },
        onEditGroup = { group -> onNavToEdit(group.id, group.name) },
        onLeaveGroup = viewModel::onLeaveGroup,
        onDismissError = viewModel::onDismissError
      )

    GroupsListUiState.Loading ->
      GroupsListLoadingScreen()
  }
}
package com.splanes.uoc.wishlify.presentation.feature.groups.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GroupsSearchUsersRoute(
  viewModel: GroupsSearchUsersViewModel,
  onFinish: (result: List<String>) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  GroupsSearchUsersScreen(
    uiState = uiState,
    onSearch = viewModel::onSearch,
    onAddUser = viewModel::onAddUser,
    onRemoveUser = viewModel::onRemoveUser,
    onSave = { users -> onFinish(users.map { it.uid }) },
    onDismissError = viewModel::onDismissError,
    onCloseInfoBanner = viewModel::onCloseInfoBanner,
    onBack = { onFinish(emptyList()) }
  )
}
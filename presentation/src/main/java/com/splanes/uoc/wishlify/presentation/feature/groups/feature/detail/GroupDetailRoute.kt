package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist

@Composable
fun GroupDetailRoute(
  viewModel: GroupDetailViewModel,
  onNavToEdit: (groupId: String, name: String) -> Unit,
  onNavToSharedWishlist: (SharedWishlist) -> Unit,
  onNavToSecretSanta: (SecretSantaEvent) -> Unit,
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
        onWishlistClick = onNavToSharedWishlist,
        onSecretSantaEventClick = onNavToSecretSanta,
        onOpenWishlistsByGroupModal = viewModel::onOpenWishlistsByGroupModal,
        onOpenSecretSantaEventsByGroupModal = viewModel::onOpenSecretSantaEventsByGroupModal,
        onCloseWishlistsByGroupModal = viewModel::onCloseWishlistsByGroupModal,
        onCloseSecretSantaEventsByGroupModal = viewModel::onCloseSecretSantaEventsByGroupModal,
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
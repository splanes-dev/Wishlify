package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.edition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WishlistsEditListRoute(
  viewModel: WishlistsEditListViewModel,
  onFinish: (created: Boolean) -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        WishlistsEditListUiSideEffect.WishlistUpdated -> onFinish(true)
      }
    }
  }

  when (val state = uiState) {
    WishlistsEditListUiState.Error ->
      WishlistsEditListErrorScreen(onCancel = { onFinish(false) })

    is WishlistsEditListUiState.Form ->
      WishlistsEditListScreen(
        uiState = state,
        onEdit = viewModel::onUpdate,
        onCreateCategory = viewModel::onCreateCategory,
        onChangeNewCategoryModalVisibility = viewModel::onChangeNewCategoryModalVisibility,
        onCancel = { onFinish(false) },
        onClearInputError = viewModel::onClearInputError,
        onDismissError = viewModel::onDismissError
      )

    WishlistsEditListUiState.Loading ->
      WishlistsEditListLoadingScreen(onCancel = { onFinish(false) })
  }
}
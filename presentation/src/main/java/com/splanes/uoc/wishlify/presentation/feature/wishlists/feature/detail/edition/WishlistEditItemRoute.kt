package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.edition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WishlistEditItemRoute(
  viewModel: WishlistEditItemViewModel,
  onFinish: (result: Boolean) -> Unit
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        WishlistEditItemUiSideEffect.ItemEdited -> onFinish(true)
      }
    }
  }

  when (val state = uiState) {
    WishlistEditItemUiState.Error ->
      WishlistEditItemErrorScreen(
        onCancel = { onFinish(false) }
      )

    is WishlistEditItemUiState.Form ->
      WishlistEditItemFormScreen(
        uiState = state,
        onEdit = viewModel::onEdit,
        onClearInputError = viewModel::onClearInputError,
        onDismissError = viewModel::onDismissError,
        onCancel = { onFinish(false) }
      )

    WishlistEditItemUiState.Loading ->
      WishlistEditItemLoadingScreen(
        onCancel = { onFinish(false) }
      )
  }


}
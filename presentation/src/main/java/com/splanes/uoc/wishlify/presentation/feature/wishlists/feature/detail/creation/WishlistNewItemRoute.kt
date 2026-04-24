package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.creation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WishlistNewItemRoute(
  viewModel: WishlistNewItemViewModel,
  onFinish: (created: Boolean) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        WishlistNewItemUiSideEffect.ItemCreated -> onFinish(true)
      }
    }
  }

  WishlistNewItemScreen(
    uiState = uiState,
    onCreate = viewModel::onCreate,
    onAutocompleteByLink = viewModel::onAutocomplete,
    onClearInputError = viewModel::onClearInputError,
    onDismissError = viewModel::onDismissError,
    onCancel = { onFinish(false) }
  )
}
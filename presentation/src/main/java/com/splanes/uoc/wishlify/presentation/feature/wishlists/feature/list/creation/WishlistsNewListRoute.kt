package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WishlistsNewListRoute(
  viewModel: WishlistsNewListViewModel,
  onFinish: (created: Boolean) -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        WishlistsNewListUiSideEffect.WishlistCreated -> onFinish(true)
      }
    }
  }

  WishlistsNewListScreen(
    uiState = uiState,
    onCreate = viewModel::onCreate,
    onCreateCategory = viewModel::onCreateCategory,
    onIsOwnWishlistChanged = viewModel::isOwnWishlistChanged,
    onChangeNewCategoryModalVisibility = viewModel::onChangeNewCategoryModalVisibility,
    onCancel = { onFinish(false) },
    onClearInputError = viewModel::onClearInputError,
    onDismissError = viewModel::onDismissError
  )
}
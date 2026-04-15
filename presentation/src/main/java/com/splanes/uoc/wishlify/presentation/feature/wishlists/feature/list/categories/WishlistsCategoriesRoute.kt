package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WishlistsCategoriesRoute(
  viewModel: WishlistsCategoriesViewModel,
  onBack: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (val state = uiState) {
    is WishlistsCategoriesUiState.Categories ->
      WishlistsCategoriesScreen(
        uiState = state,
        onBack = onBack,
        onCategoryAction = viewModel::onCategoryAction,
        onCreateOrUpdateCategory = viewModel::onCreateOrUpdateCategory,
        onDeleteCategoryConfirmed = viewModel::onDeleteCategoryConfirmed,
        onClearInputError = viewModel::onClearInputError,
        onCloseCategoryModal = viewModel::onCloseCategoryModal,
        onCloseDeleteCategoryDialog = viewModel::onCloseDeleteCategoryDialog,
        onDismissError = viewModel::onDismissError
      )

    is WishlistsCategoriesUiState.Empty ->
      WishlistsCategoriesEmptyScreen(
        uiState = state,
        onCategoryAction = viewModel::onCategoryAction,
        onCreateOrUpdateCategory = viewModel::onCreateOrUpdateCategory,
        onClearInputError = viewModel::onClearInputError,
        onCloseCategoryModal = viewModel::onCloseCategoryModal,
        onBack = onBack,
        onDismissError = viewModel::onDismissError
      )

    WishlistsCategoriesUiState.Loading ->
      WishlistsCategoriesLoadingScreen(
        onBack = onBack
      )
  }
}
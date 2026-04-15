package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ConfirmationDialog
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories.components.CategoryItem
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories.model.CategoryAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.NewCategoryBottomSheet
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistsCategoriesScreen(
  uiState: WishlistsCategoriesUiState.Categories,
  onCategoryAction: (CategoryAction) -> Unit,
  onCreateOrUpdateCategory: (name: String, color: Category.CategoryColor) -> Unit,
  onDeleteCategoryConfirmed: () -> Unit,
  onClearInputError: () -> Unit,
  onCloseCategoryModal: () -> Unit,
  onCloseDeleteCategoryDialog: () -> Unit,
  onBack: () -> Unit,
  onDismissError: () -> Unit,
) {

  val coroutineScope = rememberCoroutineScope()
  val categorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.wishlists_admin_categories_title)) },
          navigationIcon = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onBack
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back)
              )
            }
          },
        )
      },
    ) { paddings ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddings),
        contentPadding = PaddingValues(
          horizontal = 16.dp,
          vertical = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(uiState.categories) { category ->
          CategoryItem(
            category = category,
            onAction = onCategoryAction
          )
        }

        item {
          Surface(
            modifier = Modifier.heightIn(min = 70.dp),
            color = Color.Transparent,
            shape = WishlifyTheme.shapes.small,
            onClick = { onCategoryAction(CategoryAction.New) }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {

              Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "",
                tint = WishlifyTheme.colorScheme.success
              )

              Spacer(Modifier.width(16.dp))

              Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.wishlists_new_category),
                style = WishlifyTheme.typography.titleMedium,
                color = WishlifyTheme.colorScheme.success
              )
            }
          }
        }
      }
    }

    NewCategoryBottomSheet(
      isVisible = uiState.isCategoryModalVisible,
      sheetState = categorySheetState,
      initial = uiState.selectedCategory,
      error = uiState.categoryNameInputError,
      onClearInputError = onClearInputError,
      onDismiss = {
        coroutineScope.launch {
          categorySheetState.hide()
        }.invokeOnCompletion {
          onCloseCategoryModal()
        }
      },
      onCreate = onCreateOrUpdateCategory,
    )

    if (uiState.isConfirmDeleteCategoryDialogVisible) {
      ConfirmationDialog(
        onDismiss = onCloseDeleteCategoryDialog,
        onConfirm = onDeleteCategoryConfirmed
      )
    }

    uiState.error?.let { error ->
      ErrorDialog(
        uiModel = error,
        onDismiss = onDismissError,
      )
    }

    if (uiState.isLoading) {
      Loader(modifier = Modifier.fillMaxSize())
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistsCategoriesEmptyScreen(
  uiState: WishlistsCategoriesUiState.Empty,
  onCategoryAction: (CategoryAction) -> Unit,
  onCreateOrUpdateCategory: (name: String, color: Category.CategoryColor) -> Unit,
  onClearInputError: () -> Unit,
  onCloseCategoryModal: () -> Unit,
  onBack: () -> Unit,
  onDismissError: () -> Unit,
) {

  val coroutineScope = rememberCoroutineScope()
  val categorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.wishlists_admin_categories_title)) },
          navigationIcon = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onBack
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back)
              )
            }
          },
        )
      },
    ) { paddings ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(paddings)
          .padding(
            horizontal = 16.dp,
            vertical = 24.dp
          ),
      ) {

        EmptyState(
          modifier = Modifier.weight(1f),
          image = painterResource(R.drawable.wishlists_items_empty_state),
          title = stringResource(R.string.wishlists_list_empty_state_title),
          description = stringResource(R.string.wishlists_admin_categories_empty),
          button = {
            Button(
              modifier = Modifier.fillMaxWidth(),
              shapes = ButtonShape,
              colors = ButtonDefaults.buttonColors(
                contentColor = WishlifyTheme.colorScheme.onSuccess,
                containerColor = WishlifyTheme.colorScheme.success,
              ),
              onClick = { onCategoryAction(CategoryAction.New) }
            ) {
              Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "",
              )

              Spacer(Modifier.width(8.dp))

              ButtonText(text = stringResource(R.string.wishlists_new_category))
            }
          }
        )
      }
    }

    NewCategoryBottomSheet(
      isVisible = uiState.isCategoryModalVisible,
      sheetState = categorySheetState,
      error = uiState.categoryNameInputError,
      onClearInputError = onClearInputError,
      onDismiss = {
        coroutineScope.launch {
          categorySheetState.hide()
        }.invokeOnCompletion {
          onCloseCategoryModal()
        }
      },
      onCreate = onCreateOrUpdateCategory,
    )

    uiState.error?.let { error ->
      ErrorDialog(
        uiModel = error,
        onDismiss = onDismissError,
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistsCategoriesLoadingScreen(
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.wishlists_admin_categories_title)) },
        navigationIcon = {
          IconButton(
            shapes = IconButtonShape,
            onClick = onBack
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
        },
      )
    },
  ) { paddings ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(paddings)
        .padding(
          horizontal = 16.dp,
          vertical = 24.dp
        ),
      verticalArrangement = Arrangement.Center
    ) {

      Loader(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent
      )
    }
  }
}
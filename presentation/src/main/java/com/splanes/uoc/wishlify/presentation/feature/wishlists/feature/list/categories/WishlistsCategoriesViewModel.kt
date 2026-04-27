package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateCategoryUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteCategoryUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchCategoriesUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.UpdateCategoryUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories.model.CategoryAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.mapper.CategoryFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.mapper.CategoryUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.model.CategoryFormError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates category administration for wishlists, including creation, edition and deletion.
 */
class WishlistsCategoriesViewModel(
  private val fetchCategoriesUseCase: FetchCategoriesUseCase,
  private val createCategoryUseCase: CreateCategoryUseCase,
  private val updateCategoryUseCase: UpdateCategoryUseCase,
  private val deleteCategoryUseCase: DeleteCategoryUseCase,
  private val categoryUiMapper: CategoryUiMapper,
  private val categoryFormErrorMapper: CategoryFormErrorMapper,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchCategories() }
    .map { state ->
      state.toUiState(
        categoryUiMapper = categoryUiMapper,
        categoryFormErrorMapper = categoryFormErrorMapper,
        errorUiMapper = errorUiMapper,
      )
    }
    .stateIn(
      initialValue = viewModelState.value.toUiState(
        categoryUiMapper = categoryUiMapper,
        categoryFormErrorMapper = categoryFormErrorMapper,
        errorUiMapper = errorUiMapper,
      ),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  /**
   * Dispatches the selected category action to the matching flow.
   */
  fun onCategoryAction(action: CategoryAction) {
    when (action) {
      is CategoryAction.Delete -> onDeleteCategory(action.category.id)
      is CategoryAction.Edit -> onStartEdit(action.category.id)
      CategoryAction.New -> onStartCreation()
    }
  }

  /**
   * Creates a new category or updates the selected one, depending on the current modal mode.
   */
  fun onCreateOrUpdateCategory(name: String, color: Category.CategoryColor) {
    if (validateForm(name)) {
      viewModelScope.launch {
        viewModelState.update { state -> state.copy(isLoading = true) }
        val selectedCategory = viewModelState.value.selectedCategory
        val result = if (selectedCategory != null) {
          updateCategoryUseCase(
            categoryId = selectedCategory.id,
            name = name,
            color = color,
          )
        } else {
          createCategoryUseCase(name, color)
        }
        viewModelState.update { state ->
          result.fold(
            onSuccess = { category ->
              state.copy(
                isLoading = false,
                categories = buildList {
                  addAll(state.categories.filter { cat -> cat.id != category.id })
                  add(category)
                },
                isCategoryModalVisible = false,
                selectedCategory = null
              )
            },
            onFailure = { error ->
              state.copy(
                isLoading = false,
                error = error
              )
            }
          )
        }
      }
    }
  }

  /**
   * Confirms deletion of the currently selected category.
   */
  fun onDeleteCategoryConfirmed() {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      val category = viewModelState.value.selectedCategory
      if (category != null) {
        deleteCategoryUseCase(category.id)
          .onSuccess {
            viewModelState.update { state ->
              state.copy(
                isLoading = false,
                categories = state.categories - category
              )
            }
          }
          .onFailure { error ->
            viewModelState.update { state ->
              state.copy(
                isLoading = false,
                error = error
              )
            }
          }
      } else {
        viewModelState.update { state ->
          state.copy(
            isLoading = false,
            error = GenericError.Unknown()
          )
        }
      }
    }
  }

  /**
   * Closes the create or edit category modal.
   */
  fun onCloseCategoryModal() {
    viewModelState.update { state ->
      state.copy(
        isCategoryModalVisible = false,
        selectedCategory = null
      )
    }
  }

  /**
   * Closes the delete confirmation dialog.
   */
  fun onCloseDeleteCategoryDialog() {
    viewModelState.update { state ->
      state.copy(
        isConfirmDeleteCategoryDialogVisible = false,
        selectedCategory = null
      )
    }
  }

  /**
   * Clears the current category name validation error.
   */
  fun onClearInputError() {
    viewModelState.update { state ->
      state.copy(categoryNameInputError = null)
    }
  }

  /**
   * Clears the current UI error.
   */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /**
   * Loads the available wishlist categories.
   */
  private suspend fun fetchCategories() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    fetchCategoriesUseCase()
      .onSuccess { categories ->
        viewModelState.update { state ->
          state.copy(
            isLoadingFullscreen = false,
            categories = categories
          )
        }
      }
      .onFailure { error ->
        viewModelState.update { state ->
          state.copy(
            isLoadingFullscreen = false,
            error = error
          )
        }
      }
  }

  /**
   * Opens the category modal in creation mode.
   */
  private fun onStartCreation() {
    viewModelState.update { state ->
      state.copy(isCategoryModalVisible = true)
    }
  }

  /**
   * Opens the category modal in edition mode for the selected category.
   */
  private fun onStartEdit(id: String) {
    viewModelState.update { state ->
      state.copy(
        isCategoryModalVisible = true,
        selectedCategory = state.categories.find { it.id == id }
      )
    }
  }

  /**
   * Opens the delete confirmation dialog for the selected category.
   */
  private fun onDeleteCategory(id: String) {
    viewModelState.update { state ->
      state.copy(
        isConfirmDeleteCategoryDialogVisible = true,
        selectedCategory = state.categories.find { category -> category.id == id }
      )
    }
  }

  /**
   * Validates the category form.
   */
  private fun validateForm(name: String): Boolean {
    val currentState = viewModelState.value
    val error = when {
      name.count() !in 3..20 ->
        CategoryFormError.NameLength

      currentState.categories.any { it.name.equals(name, ignoreCase = true) } ->
        CategoryFormError.AlreadyExists

      else -> null
    }

    viewModelState.update { state -> state.copy(categoryNameInputError = error) }

    return error == null
  }

  private data class ViewModelState(
    val categories: List<Category> = emptyList(),
    val isConfirmDeleteCategoryDialogVisible: Boolean = false,
    val isCategoryModalVisible: Boolean = false,
    val categoryNameInputError: CategoryFormError? = null,
    val selectedCategory: Category? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    /**
     * Maps internal state to the categories UI contract.
     */
    fun toUiState(
      categoryUiMapper: CategoryUiMapper,
      categoryFormErrorMapper: CategoryFormErrorMapper,
      errorUiMapper: ErrorUiMapper
    ): WishlistsCategoriesUiState =
      when {
        isLoadingFullscreen ->
          WishlistsCategoriesUiState.Loading

        categories.isEmpty() ->
          WishlistsCategoriesUiState.Empty(
            categoryNameInputError = categoryNameInputError?.let(categoryFormErrorMapper::map),
            isCategoryModalVisible = isCategoryModalVisible,
            error = error?.let(errorUiMapper::map),
          )

        else ->
          WishlistsCategoriesUiState.Categories(
            categories = categories.map(categoryUiMapper::map),
            isConfirmDeleteCategoryDialogVisible = isConfirmDeleteCategoryDialogVisible,
            isCategoryModalVisible = isCategoryModalVisible,
            categoryNameInputError = categoryNameInputError?.let(categoryFormErrorMapper::map),
            selectedCategory = selectedCategory,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }
  }
}

package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateCategoryUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchCategoriesUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.mapper.WishlistFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.mapper.WishlistFormUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model.DescriptionWishlistFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model.NameWishlistFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model.TargetWishlistFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model.WishlistsNewListForm
import com.splanes.uoc.wishlify.presentation.feature.wishlists.mapper.CategoryFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.mapper.CategoryUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.model.CategoryFormError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WishlistsNewListViewModel(
  isOwnWishlist: Boolean,
  private val createWishlistUseCase: CreateWishlistUseCase,
  private val createCategoryUseCase: CreateCategoryUseCase,
  private val fetchCategoriesUseCase: FetchCategoriesUseCase,
  private val wishlistFormUiMapper: WishlistFormUiMapper,
  private val wishlistFormErrorMapper: WishlistFormErrorMapper,
  private val categoryFormErrorMapper: CategoryFormErrorMapper,
  private val categoryUiMapper: CategoryUiMapper,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState(isOwnWishlist))

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchCategories() }
    .map { state ->
      state.toUiState(
        categoryUiMapper = categoryUiMapper,
        wishlistFormErrorMapper = wishlistFormErrorMapper,
        categoryFormErrorMapper = categoryFormErrorMapper,
        errorUiMapper = errorUiMapper,
      )
    }
    .stateIn(
      initialValue = viewModelState.value.toUiState(
        categoryUiMapper = categoryUiMapper,
        wishlistFormErrorMapper = wishlistFormErrorMapper,
        categoryFormErrorMapper = categoryFormErrorMapper,
        errorUiMapper = errorUiMapper,
      ),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<WishlistsNewListUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  fun onCreate(form: WishlistsNewListForm) {
    if (validateForm(form)) {
      val currentState = viewModelState.value
      val request = wishlistFormUiMapper.requestOf(
        isOwnWishlist = currentState.isOwnWishlist,
        categories = currentState.categories,
        editorLink = currentState.editorLink,
        form = form
      )

      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        createWishlistUseCase(request)
          .onSuccess {
            viewModelState.update { state -> state.copy(isLoading = false) }
            uiSideEffectChannel.send(WishlistsNewListUiSideEffect.WishlistCreated)
          }
          .onFailure { error ->
            viewModelState.update { state ->
              state.copy(
                isLoading = false,
                error = error
              )
            }
          }
      }
    }
  }

  fun onCreateCategory(name: String, color: Category.CategoryColor) {
    if (validateNewCategory(name)) {
      viewModelScope.launch {
        viewModelState.update { state -> state.copy(isLoading = true) }
        createCategoryUseCase(name = name, color = color)
          .onSuccess { category ->
            viewModelState.update { state ->
              state.copy(
                isLoading = false,
                categories = state.categories + category
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

        onChangeNewCategoryModalVisibility(visible = false)
      }
    }
  }

  fun isOwnWishlistChanged(isOwn: Boolean) {
    viewModelState.update { state -> state.copy(isOwnWishlist = isOwn) }
  }

  fun onChangeNewCategoryModalVisibility(visible: Boolean) {
    viewModelState.update { state ->
      state.copy(
        isNewCategoryModalOpen = visible,
        newCategoryNameError = null
      )
    }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  fun onClearInputError(input: WishlistsNewListForm.Input) {
    when (input) {
      WishlistsNewListForm.Input.Name ->
        viewModelState.update { state -> state.copy(nameError = null) }

      WishlistsNewListForm.Input.Target ->
        viewModelState.update { state -> state.copy(targetError = null) }

      WishlistsNewListForm.Input.Description ->
        viewModelState.update { state -> state.copy(descriptionError = null) }

      WishlistsNewListForm.Input.NewCategoryName ->
        viewModelState.update { state -> state.copy(newCategoryNameError = null) }
    }
  }

  private suspend fun fetchCategories() {
    viewModelState.update { state -> state.copy(isLoading = true) }
    fetchCategoriesUseCase()
      .onSuccess { categories ->
        viewModelState.update { state ->
          state.copy(
            isLoading = false,
            categories = categories
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
  }

  private fun validateForm(form: WishlistsNewListForm): Boolean {
    val currentState = viewModelState.value

    val nameError = when {
      form.name.count() !in 3..20 -> NameWishlistFormError.Length
      else -> null
    }

    val targetError = when {
      !currentState.isOwnWishlist && form.target.isNullOrBlank() ->
        TargetWishlistFormError.Blank

      else -> null
    }

    val descriptionError = when {
      form.description?.count() !in 3..200 -> DescriptionWishlistFormError.Length
      else -> null
    }

    viewModelState.update { state ->
      state.copy(
        nameError = nameError,
        targetError = targetError,
        descriptionError = descriptionError,
      )
    }

    return nameError == null && targetError == null && descriptionError == null
  }

  private fun validateNewCategory(name: String): Boolean {
    val currentState = viewModelState.value
    val error = when {
      name.count() !in 3..20 ->
        CategoryFormError.NameLength

      currentState.categories.any { it.name.equals(name, ignoreCase = true) } ->
        CategoryFormError.AlreadyExists

      else -> null
    }

    viewModelState.update { state -> state.copy(newCategoryNameError = error) }

    return error == null
  }

  private data class ViewModelState(
    val isOwnWishlist: Boolean,
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val editorLink: InviteLink = InviteLink.new(InviteLink.WishlistsEditor),
    val nameError: NameWishlistFormError? = null,
    val targetError: TargetWishlistFormError? = null,
    val descriptionError: DescriptionWishlistFormError? = null,
    val newCategoryNameError: CategoryFormError? = null,
    val isNewCategoryModalOpen: Boolean = false,
    val error: Throwable? = null
  ) {
    fun toUiState(
      categoryUiMapper: CategoryUiMapper,
      wishlistFormErrorMapper: WishlistFormErrorMapper,
      categoryFormErrorMapper: CategoryFormErrorMapper,
      errorUiMapper: ErrorUiMapper,
    ) =
      WishlistsNewListUiState(
        categories = categories.map(categoryUiMapper::map),
        isOwnWishlist = isOwnWishlist,
        editorLink = editorLink.asUrl(),
        nameError = nameError?.let(wishlistFormErrorMapper::map),
        targetError = targetError?.let(wishlistFormErrorMapper::map),
        descriptionError = descriptionError?.let(wishlistFormErrorMapper::map),
        newCategoryNameError = newCategoryNameError?.let(categoryFormErrorMapper::map),
        isNewCategoryModalOpen = isNewCategoryModalOpen,
        isLoading = isLoading,
        error = error?.let(errorUiMapper::map)
      )
  }
}
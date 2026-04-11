package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.edition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateCategoryUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchCategoriesUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.UpdateWishlistUseCase
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
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WishlistsEditListViewModel(
  private val wishlistId: String,
  private val fetchCategoriesUseCase: FetchCategoriesUseCase,
  private val fetchWishlistUseCase: FetchWishlistUseCase,
  private val updateWishlistUseCase: UpdateWishlistUseCase,
  private val createCategoryUseCase: CreateCategoryUseCase,
  private val categoryUiMapper: CategoryUiMapper,
  private val wishlistFormUiMapper: WishlistFormUiMapper,
  private val wishlistFormErrorMapper: WishlistFormErrorMapper,
  private val categoryFormErrorMapper: CategoryFormErrorMapper,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchWishlistAndCategories(wishlistId) }
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

  private val uiSideEffectChannel = Channel<WishlistsEditListUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()



  fun onUpdate(form: WishlistsNewListForm, isOwnWishlist: Boolean) {
    if (validateForm(form, isOwnWishlist)) {
      val currentState = viewModelState.value
      val wishlist = currentState.wishlist ?: return // Should not happen
      val request = wishlistFormUiMapper.updateWishlistRequestOf(
        currentWishlist = wishlist,
        isOwnWishlist = isOwnWishlist,
        categories = currentState.categories,
        editorLink = currentState.editorLink,
        form = form
      )

      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        updateWishlistUseCase(request)
          .onSuccess {
            viewModelState.update { state -> state.copy(isLoading = false) }
            uiSideEffectChannel.send(WishlistsEditListUiSideEffect.WishlistUpdated)
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

  private suspend fun fetchWishlistAndCategories(wishlistId: String) {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    coroutineScope {
      val categoriesDeferred = async { fetchCategoriesUseCase() }
      val wishlistDeferred = async { fetchWishlistUseCase(wishlistId) }
      val categoriesResult = categoriesDeferred.await()
      val wishlistResult = wishlistDeferred.await()

      viewModelState.update { state ->
        state.copy(
          wishlist = wishlistResult.getOrNull(),
          categories = categoriesResult.getOrDefault(emptyList()),
          isLoadingFullscreen = false
        )
      }
    }
  }

  private fun validateForm(form: WishlistsNewListForm, isOwnWishlist: Boolean): Boolean {
    val nameError = when {
      form.name.count() !in 3..20 -> NameWishlistFormError.Length
      else -> null
    }

    val targetError = when {
      !isOwnWishlist && form.target.isNullOrBlank() ->
        TargetWishlistFormError.Blank

      else -> null
    }

    val descriptionError = when {
      form.description?.isNotBlank() == true && form.description.count() !in 3..200 -> DescriptionWishlistFormError.Length
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
    val wishlist: Wishlist? = null,
    val isLoading: Boolean = false,
    val isLoadingFullscreen: Boolean = true,
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
    ) = when {
      isLoadingFullscreen -> {
        WishlistsEditListUiState.Loading
      }

      wishlist == null -> {
        WishlistsEditListUiState.Error
      }

      else -> {
        WishlistsEditListUiState.Form(
          wishlist = wishlist,
          categories = categories.map(categoryUiMapper::map),
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
  }
}
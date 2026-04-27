package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.UnshareWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.AddWishlistEditorFromLinkUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateCategoryUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchCategoriesUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistsUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistNewItemByShare
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsFilter
import com.splanes.uoc.wishlify.presentation.feature.wishlists.mapper.CategoryFormErrorMapper
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
 * Coordinates the main wishlists list, including filters, deeplink actions and category creation
 * from the list flow.
 */
class WishlistsListViewModel(
  private val fetchWishlistsUseCase: FetchWishlistsUseCase,
  private val deleteWishlistUseCase: DeleteWishlistUseCase,
  private val fetchCategoriesUseCase: FetchCategoriesUseCase,
  private val unshareWishlistUseCase: UnshareWishlistUseCase,
  private val addWishlistEditorFromLinkUseCase: AddWishlistEditorFromLinkUseCase,
  private val categoryFormErrorMapper: CategoryFormErrorMapper,
  private val createCategoryUseCase: CreateCategoryUseCase,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())
  private var itemByShare: WishlistNewItemByShare? = null

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchWishlistsAndCategories() }
    .map { state -> state.toUiState(errorUiMapper, categoryFormErrorMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper, categoryFormErrorMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  /**
   * Creates a category from the list flow and appends it to the currently loaded categories.
   */
  fun onCreateCategory(name: String, color: Category.CategoryColor) {
    if (validateCategoryForm(name)) {
      viewModelScope.launch {
        viewModelState.update { state -> state.copy(isLoading = true) }
        val result = createCategoryUseCase(name, color)
        viewModelState.update { state ->
          result.fold(
            onSuccess = { category ->
              state.copy(
                isLoading = false,
                categories = buildList {
                  addAll(state.categories.filter { cat -> cat.id != category.id })
                  add(category)
                },
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
   * Joins the current user as editor from an invitation token and refreshes the wishlists list.
   */
  fun onAddToEditorsDeeplinkOpened(token: String) {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    viewModelScope.launch {
      addWishlistEditorFromLinkUseCase(token)
        .onSuccess { fetchWishlists() }
        .onFailure { viewModelState.update { state -> state.copy(isLoadingFullscreen = false) } }
    }
  }

  /**
   * Stores a shared URL so the user can choose which wishlist should receive the new item.
   */
  fun onNewItemByUrl(url: String) {
    itemByShare = WishlistNewItemByShare.Url(url)
    viewModelScope.launch { fetchWishlistsAndCategories() }
  }

  /**
   * Stores a shared image URI so the user can choose which wishlist should receive the new item.
   */
  fun onNewItemByUri(uri: String) {
    itemByShare = WishlistNewItemByShare.Uri(uri)
    viewModelScope.launch { fetchWishlistsAndCategories() }
  }

  /**
   * Updates the currently active filters for the wishlists list.
   */
  fun onUpdateFilters(filtersState: WishlistsFiltersState) {
    viewModelState.update { state -> state.copy(filtersState = filtersState) }
  }

  /**
   * Refreshes the list after returning from wishlist creation.
   */
  fun onNewWishlistResult(created: Boolean) {
    if (created) {
      viewModelScope.launch {
        fetchWishlists()
      }
    }
  }

  /**
   * Refreshes the list after returning from a wishlist update flow.
   */
  fun onUpdateWishlistResult(updated: Boolean) {
    if (updated) {
      viewModelScope.launch {
        fetchWishlists()
      }
    }
  }

  /**
   * Stores the feedback message shown after sharing a wishlist.
   */
  fun onWishlistShared(name: String) {
    viewModelState.update { state -> state.copy(sharedWishlistFeedback = name) }
  }

  /**
   * Deletes the selected wishlist from the list flow.
   */
  fun onDeleteWishlist(wishlist: Wishlist) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      deleteWishlistUseCase(wishlist)
        .onSuccess {
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              wishlists = state.wishlists - wishlist
            )
          }
        }
        .onFailure { error ->
          viewModelState.update { state ->
            state.copy(isLoading = false, error = error)
          }
        }
    }
  }

  /**
   * Converts a shared wishlist back to a private wishlist and removes the shared projection from
   * the current list.
   */
  fun onSharedBackToPrivate(wishlist: Wishlist) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      unshareWishlistUseCase(wishlist.id)
        .onSuccess {
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              wishlists = state.wishlists - wishlist,
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
  }

  /**
   * Closes the wishlist selection modal used when creating a new item from a shared payload.
   */
  fun onCloseWishlistSelectionModal() {
    itemByShare = null
    viewModelState.update { state ->
      state.copy(
        isWishlistSelectionModalOpen = false,
        wishlistNewItemByShare = null,
      )
    }
  }

  /**
   * Clears the validation error associated with the new category name field.
   */
  fun onClearNewCategoryNameError() {
    viewModelState.update { state -> state.copy(newCategoryNameError = null) }
  }

  fun onClearSharedWishlistFeedback() {
    viewModelState.update { state -> state.copy(sharedWishlistFeedback = null) }
  }

  /**
   * Clears the current UI error.
   */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /**
   * Validates the category form shown from the list flow.
   */
  private fun validateCategoryForm(name: String): Boolean {
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

  /**
   * Loads the current wishlists.
   */
  private suspend fun fetchWishlists() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    fetchWishlistsUseCase()
      .onSuccess { wishlists ->
        viewModelState.update { state ->
          state.copy(
            isLoadingFullscreen = false,
            wishlists = wishlists,
          )
        }
      }
      .onFailure { error ->
        viewModelState.update { state ->
          state.copy(
            isLoadingFullscreen = false,
            error = error,
          )
        }
      }
  }

  /**
   * Loads wishlists and categories together, also resolving whether a shared payload should open
   * the wishlist selection modal.
   */
  private suspend fun fetchWishlistsAndCategories() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val wishlistsResult = fetchWishlistsUseCase()
    val categoriesResult = fetchCategoriesUseCase()
    viewModelState.update { state ->
      state.copy(
        isLoadingFullscreen = false,
        wishlists = wishlistsResult.getOrDefault(emptyList()),
        categories = categoriesResult.getOrDefault(emptyList()),
        isWishlistSelectionModalOpen = itemByShare != null,
        wishlistNewItemByShare = itemByShare,
        error = wishlistsResult.exceptionOrNull()
      )
    }
  }

  private data class ViewModelState(
    val wishlists: List<Wishlist> = emptyList(),
    val filtersState: WishlistsFiltersState = WishlistsFiltersState(),
    val categories: List<Category> = emptyList(),
    val sharedWishlistFeedback: String? = null,
    val newCategoryNameError: CategoryFormError? = null,
    val isWishlistSelectionModalOpen: Boolean = false,
    val wishlistNewItemByShare: WishlistNewItemByShare? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {

    /**
     * Maps internal state to the list UI contract.
     */
    fun toUiState(
      errorUiMapper: ErrorUiMapper,
      categoryFormErrorMapper: CategoryFormErrorMapper,
    ) =
      when {
        isLoadingFullscreen ->
          WishlistsListUiState.Loading

        wishlists.withFilters(filtersState).isEmpty() ->
          WishlistsListUiState.Empty(
            filtersState = filtersState,
            categories = categories,
            sharedWishlistFeedback = sharedWishlistFeedback,
            newCategoryNameError = newCategoryNameError?.let(categoryFormErrorMapper::map),
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )

        else ->
          WishlistsListUiState.Listing(
            filtersState = filtersState,
            categories = categories,
            wishlists = wishlists.withFilters(filtersState).sort(),
            sharedWishlistFeedback = sharedWishlistFeedback,
            isWishlistSelectionModalOpen = isWishlistSelectionModalOpen,
            wishlistNewItemByShare = wishlistNewItemByShare,
            newCategoryNameError = newCategoryNameError?.let(categoryFormErrorMapper::map),
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }
  }
}

/**
 * Applies the currently selected filters to the wishlists list.
 */
private fun List<Wishlist>.withFilters(filters: WishlistsFiltersState) =
  this
    .filter { wishlist ->
      when (filters.target) {
        WishlistsFilter.Own -> wishlist is Wishlist.Own
        WishlistsFilter.ThirdParty -> wishlist is Wishlist.ThirdParty
        else -> true
      }
    }
    .filter { wishlist ->
      when (val filter = filters.category) {
        is WishlistsFilter.Categories -> wishlist.category?.category in filter.values
        else -> true
      }
    }
    .filter { wishlist ->
      when (filters.shareStatus) {
        WishlistsFilter.NotShared -> wishlist !is Wishlist.Shared
        WishlistsFilter.OnSecretSantaEvent -> wishlist is Wishlist.Shared && wishlist.event is Wishlist.SecretSantaEvent
        WishlistsFilter.OnSharedWishlist -> wishlist is Wishlist.Shared && wishlist.event is Wishlist.SharedWishlistEvent
        else -> true
      }
    }
    .filter { wishlist ->
      when (filters.availability) {
        WishlistsFilter.ItemsAvailable -> wishlist.numOfNonPurchasedItems > 0
        WishlistsFilter.ItemsNotAvailable -> wishlist.numOfNonPurchasedItems == 0
        else -> true
      }
    }

/**
 * Sorts wishlists by completion status and then by creation date.
 */
private fun List<Wishlist>.sort() = sortedWith(
  compareBy<Wishlist> { it.isFinished() }
    .thenByDescending { it.createdAt }
)

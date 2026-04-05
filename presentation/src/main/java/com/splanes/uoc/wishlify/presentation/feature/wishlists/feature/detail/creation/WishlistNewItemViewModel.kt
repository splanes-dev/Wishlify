package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.creation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateWishlistItemUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.mapper.WishlistItemFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.mapper.WishlistItemFormUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.AmountWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.DescriptionWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.LinkWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.NameWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.PriceWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.StoreWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.TagsWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemForm
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemFormErrors
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

class WishlistNewItemViewModel(
  wishlistId: String,
  private val link: String?,
  private val createWishlistItemUseCase: CreateWishlistItemUseCase,
  private val formErrorMapper: WishlistItemFormErrorMapper,
  private val formUiMapper: WishlistItemFormUiMapper,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState(wishlistId))

  val uiState = viewModelState.asStateFlow()
    .onStart { tryAutofillByLink(link) }
    .map { state ->
      state.toUiState(
        formErrorMapper = formErrorMapper,
        errorUiMapper = errorUiMapper,
      )
    }
    .stateIn(
      initialValue = viewModelState.value.toUiState(
        formErrorMapper = formErrorMapper,
        errorUiMapper = errorUiMapper,
      ),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<WishlistNewItemUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  fun onCreate(form: WishlistItemForm) {
    if (validateForm(form)) {
      val currentState = viewModelState.value
      val request = formUiMapper.creationRequestOf(
        wishlistId = currentState.wishlistId,
        form = form
      )
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        createWishlistItemUseCase(request)
          .onSuccess {
            viewModelState.update { state -> state.copy(isLoading = false) }
            uiSideEffectChannel.send(WishlistNewItemUiSideEffect.ItemCreated)
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

  fun onClearInputError(input: WishlistItemForm.Input) {
    viewModelState.update { state ->
      when (input) {
        WishlistItemForm.Input.Name ->
          state.copy(formErrors = state.formErrors.copy(name = null))

        WishlistItemForm.Input.Store ->
          state.copy(formErrors = state.formErrors.copy(store = null))

        WishlistItemForm.Input.Price ->
          state.copy(formErrors = state.formErrors.copy(unitPrice = null))

        WishlistItemForm.Input.Amount ->
          state.copy(formErrors = state.formErrors.copy(amount = null))

        WishlistItemForm.Input.Link ->
          state.copy(formErrors = state.formErrors.copy(link = null))

        WishlistItemForm.Input.Tags ->
          state.copy(formErrors = state.formErrors.copy(tags = null))

        WishlistItemForm.Input.Description ->
          state.copy(formErrors = state.formErrors.copy(description = null))

        else -> state
      }
    }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private suspend fun tryAutofillByLink(link: String?) {

  }

  private fun validateForm(form: WishlistItemForm): Boolean = with(form) {
    val nameError = when {
      name.count() !in 3..50 -> NameWishlistItemFormError.Length
      else -> null
    }

    val descriptionError = when {
      description.isNotBlank() && description.count() !in 3..200 ->
        DescriptionWishlistItemFormError.Length

      else -> null
    }

    val storeError = when {
      store.isNotBlank() && store.count() !in 3..30 ->
        StoreWishlistItemFormError.Length

      else -> null
    }

    val priceError = when {
      unitPrice.isNaN() -> PriceWishlistItemFormError.Invalid
      unitPrice == 0f -> PriceWishlistItemFormError.Blank
      else -> null
    }

    val amountError = when {
      unitPrice == 0f -> AmountWishlistItemFormError.Invalid
      else -> null
    }

    val linkError = when {
      link.isNotBlank() && !link.matches(Patterns.WEB_URL.toRegex()) -> LinkWishlistItemFormError.Invalid
      else -> null
    }

    val tagsError = when {
      tags.isNotBlank() && tags.split(",").count() > 3 -> TagsWishlistItemFormError.Count
      else -> null
    }

    viewModelState.update { state ->
      state.copy(
        formErrors = state.formErrors.copy(
          name = nameError,
          description = descriptionError,
          store = storeError,
          unitPrice = priceError,
          amount = amountError,
          link = linkError,
          tags = tagsError
        )
      )
    }

    nameError == null &&
        descriptionError == null &&
        storeError == null &&
        priceError == null &&
        amountError == null &&
        linkError == null &&
        tagsError == null
  }

  private data class ViewModelState(
    val wishlistId: String,
    val form: WishlistItemForm = WishlistItemForm(),
    val formErrors: WishlistItemFormErrors = WishlistItemFormErrors(),
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    fun toUiState(
      formErrorMapper: WishlistItemFormErrorMapper,
      errorUiMapper: ErrorUiMapper,
    ) =
      WishlistNewItemUiState(
        form = form,
        formErrors = formErrorMapper.map(formErrors),
        isLoading = isLoading,
        error = error?.let(errorUiMapper::map)
      )
  }
}
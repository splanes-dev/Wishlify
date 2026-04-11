package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.FetchGroupsUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.ShareWishlistUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.mapper.WishlistShareFormUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.mapper.WishlistShareUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.model.DateWishlistShareFormError
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class WishlistShareViewModel(
  private val wishlistId: String,
  private val fetchWishlistUseCase: FetchWishlistUseCase,
  private val fetchGroupsUseCase: FetchGroupsUseCase,
  private val shareWishlistUseCase: ShareWishlistUseCase,
  private val formUiMapper: WishlistShareFormUiMapper,
  private val wishlistShareUiMapper: WishlistShareUiMapper,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchWishlistAndGroups(wishlistId) }
    .map { state ->
      state.toUiState(
        formUiMapper = formUiMapper,
        errorUiMapper = errorUiMapper,
      )
    }
    .stateIn(
      initialValue = viewModelState.value.toUiState(
        formUiMapper = formUiMapper,
        errorUiMapper = errorUiMapper,
      ),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<WishlistShareUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  fun onShare(
    group: Group?,
    editorsCanSeeUpdates: Boolean,
    deadline: Long
  ) {
    if (validateForm(deadline)) {
      viewModelState.update { state -> state.copy(isLoading = true) }

      val currentState = viewModelState.value
      val request = wishlistShareUiMapper.requestOf(
        wishlist = currentState.wishlist
          ?: error("Wishlist is null. At this point this shouldn't be possible"),
        group = group,
        inviteLink = currentState.shareLink,
        editorsCanSeeUpdates = editorsCanSeeUpdates,
        deadline = deadline
      )

      viewModelScope.launch {
        shareWishlistUseCase(request)
          .onSuccess {
            viewModelState.update { state -> state.copy(isLoading = false) }
            uiSideEffectChannel.send(WishlistShareUiSideEffect.WishlistShared(currentState.wishlist.title))
          }
          .onFailure { error ->
            viewModelState.update { state ->
              state.copy(
                error = error,
                isLoading = false
              )
            }
          }
      }
    }
  }

  fun onGroupCreationResult(created: Boolean) {
    if (created) {
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        val result = fetchGroupsUseCase()
        viewModelState.update { state ->
          state.copy(
            groups = result.getOrDefault(state.groups),
            isLoading = false,
            error = result.exceptionOrNull()
          )
        }
      }
    }
  }

  fun onClearDateError() {
    viewModelState.update { state -> state.copy(inputDateError = null) }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private suspend fun fetchWishlistAndGroups(id: String) {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    coroutineScope {
      val wishlistResultDeferred = async { fetchWishlistUseCase(wishlistId = id) }
      val groupsResultDeferred = async { fetchGroupsUseCase() }
      val wishlistResult = wishlistResultDeferred.await()
      val groupsResult = groupsResultDeferred.await()

      viewModelState.update { state ->
        state.copy(
          wishlist = wishlistResult.getOrNull(),
          groups = groupsResult.getOrDefault(emptyList()),
          isLoadingFullscreen = false,
        )
      }
    }
  }

  private fun validateForm(deadline: Long): Boolean {
    val error = when {
      deadline == 0L -> DateWishlistShareFormError.Blank
      !isValidDate(deadline) -> DateWishlistShareFormError.Invalid
      else -> null
    }

    viewModelState.update { state ->
      state.copy(inputDateError = error)
    }

    return error == null
  }

  private fun isValidDate(millis: Long): Boolean {
    val zone = ZoneId.systemDefault()

    val selectedDate = Instant.ofEpochMilli(millis)
      .atZone(zone)
      .toLocalDate()

    val today = LocalDate.now(zone)
    val maxDate = today.plusMonths(6)

    return !selectedDate.isBefore(today) && !selectedDate.isAfter(maxDate)
  }

  private data class ViewModelState(
    val wishlist: Wishlist? = null,
    val groups: List<Group.Basic> = emptyList(),
    val shareLink: InviteLink = InviteLink.new(InviteLink.WishlistShare),
    val inputDateError: DateWishlistShareFormError? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    fun toUiState(
      formUiMapper: WishlistShareFormUiMapper,
      errorUiMapper: ErrorUiMapper
    ) =
      when {
        isLoadingFullscreen -> {
          WishlistShareUiState.Loading
        }

        wishlist == null -> {
          WishlistShareUiState.Error
        }

        else -> {
          WishlistShareUiState.Share(
            wishlist = wishlist,
            groups = groups,
            shareLink = shareLink.asUrl(),
            inputDateError = inputDateError?.let(formUiMapper::map),
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
        }
      }
  }
}
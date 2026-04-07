package com.splanes.uoc.wishlify.presentation.feature.groups.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.SearchUserUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupsSearchUsersViewModel(
  private val searchUserUseCase: SearchUserUseCase,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  fun onSearch(query: String) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      val results = searchUserUseCase(query)
      viewModelState.update { state ->
        state.copy(
          searchQuery = query,
          results = results.getOrDefault(emptyList()),
          isLoading = false,
          error = results.exceptionOrNull()
        )
      }
    }
  }

  fun onAddUser(user: User.Basic) {
    viewModelState.update { state ->
      val users = (state.added + user).distinctBy { it.uid }
      state.copy(added = users)
    }
  }

  fun onRemoveUser(user: User.Basic) {
    viewModelState.update { state -> state.copy(added = state.added - user) }
  }

  fun onCloseInfoBanner() {
    viewModelState.update { state -> state.copy(isInfoBannerVisible = false) }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private data class ViewModelState(
    val searchQuery: String = "",
    val results: List<User.Basic> = emptyList(),
    val added: List<User.Basic> = emptyList(),
    val isInfoBannerVisible: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    fun toUiState(errorUiMapper: ErrorUiMapper) =
      GroupsSearchUsersUiState(
        searchQuery = searchQuery,
        results = results,
        added = added,
        isInfoBannerVisible = isInfoBannerVisible,
        isLoading = isLoading,
        error = error?.let(errorUiMapper::map),
      )
  }
}
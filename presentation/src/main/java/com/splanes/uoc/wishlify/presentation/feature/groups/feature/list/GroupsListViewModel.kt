package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.FetchGroupsUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupsListViewModel(
  private val fetchGroupsUseCase: FetchGroupsUseCase,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchGroups() }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  fun onCreateGroupResult(created: Boolean) {
    if (created) {
      viewModelScope.launch {
        fetchGroups()
      }
    }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private suspend fun fetchGroups() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchGroupsUseCase()
    viewModelState.update { state ->
      state.copy(
        groups = result.getOrDefault(emptyList()),
        isLoadingFullscreen = false,
        error = result.exceptionOrNull()
      )
    }
  }

  private data class ViewModelState(
    val groups: List<Group.Basic> = emptyList(),
    val isLoadingFullscreen: Boolean = false,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    fun toUiState(errorUiMapper: ErrorUiMapper) =
      when {
        isLoadingFullscreen ->
          GroupsListUiState.Loading

        groups.isEmpty() ->
          GroupsListUiState.Empty(
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )

        else ->
          GroupsListUiState.Groups(
            groups = groups,
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }
  }
}
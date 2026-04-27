package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.model.UpdateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.FetchGroupsUseCase
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.UpdateGroupUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel that drives the groups list flow.
 *
 * It loads the current groups, refreshes them after nested flows and handles
 * the leave-group action from the list itself.
 */
class GroupsListViewModel(
  private val fetchGroupsUseCase: FetchGroupsUseCase,
  private val updateGroupUseCase: UpdateGroupUseCase,
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

  /** Reloads the groups list after returning from the creation flow. */
  fun onCreateGroupResult(created: Boolean) {
    if (created) {
      viewModelScope.launch {
        fetchGroups()
      }
    }
  }

  /** Reloads the groups list after a nested update flow finishes successfully. */
  fun onGroupUpdated() {
    viewModelScope.launch {
      fetchGroups()
    }
  }

  /** Removes the current user from the provided group and refreshes the list. */
  fun onLeaveGroup(group: Group) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      val request = UpdateGroupRequest(
        id = group.id,
        name = group.name,
        members = group.membersUid,
        image = group.photoUrl?.let(ImageMediaRequest::Url),
        includeCurrentUser = false
      )

      updateGroupUseCase(request)
        .onSuccess {
          viewModelState.update { state -> state.copy(isLoading = false) }
          fetchGroups()
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

  /** Clears the currently displayed error dialog. */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /** Fetches the current list of groups and updates the fullscreen state. */
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
    val isLoadingFullscreen: Boolean = true,
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
            groups = groups.sorted(),
            isLoading = isLoading,
            error = error?.let(errorUiMapper::map)
          )
      }

    private fun List<Group.Basic>.sorted() = sortedByDescending { !it.isInactive }
  }
}

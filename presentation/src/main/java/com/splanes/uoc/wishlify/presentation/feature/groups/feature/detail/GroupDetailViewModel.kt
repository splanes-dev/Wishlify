package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.model.UpdateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.FetchGroupUseCase
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.UpdateGroupUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaEventsUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistsUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
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

/**
 * ViewModel that drives the group detail flow.
 *
 * It resolves the selected group, handles leave-group actions and lazily loads
 * the shared wishlists and Secret Santa events linked to the same group.
 */
class GroupDetailViewModel(
  private val groupId: String,
  groupName: String,
  private val fetchGroupUseCase: FetchGroupUseCase,
  private val updateGroupUseCase: UpdateGroupUseCase,
  private val fetchSecretSantaEventsUseCase: FetchSecretSantaEventsUseCase,
  private val fetchSharedWishlistsUseCase: FetchSharedWishlistsUseCase,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState(groupName))
  val uiState = viewModelState.asStateFlow()
    .onStart { fetchGroup() }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<GroupDetailUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  /** Reloads the group after returning from a nested edit flow. */
  fun onGroupUpdated() {
    viewModelScope.launch {
      fetchGroup()
    }
  }

  /** Removes the current user from the provided group. */
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
          uiSideEffectChannel.send(GroupDetailUiSideEffect.GroupUpdated)
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

  /** Opens the shared-wishlists modal, loading its data on first use. */
  fun onOpenWishlistsByGroupModal() {
    val currentState = viewModelState.value
    if (currentState.wishlistsByGroup.isEmpty()) {
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        fetchSharedWishlistsUseCase()
          .onSuccess { wishlists ->
            viewModelState.update { state ->
              state.copy(
                isLoading = false,
                isWishlistsByGroupsModalOpen = true,
                wishlistsByGroup = wishlists.filter { it.group?.id == currentState.group?.id },
              )
            }
          }
          .onFailure { error ->
            viewModelState.update { state ->
              state.copy(
                isLoading = false,
                error = error,
              )
            }
          }
      }
    } else {
      viewModelState.update { state -> state.copy(isWishlistsByGroupsModalOpen = true) }
    }
  }

  /** Opens the Secret Santa events modal, loading its data on first use. */
  fun onOpenSecretSantaEventsByGroupModal() {
    val currentState = viewModelState.value
    if (currentState.secretSantaEventsByGroup.isEmpty()) {
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        fetchSecretSantaEventsUseCase()
          .onSuccess { events ->
            viewModelState.update { state ->
              state.copy(
                isLoading = false,
                isSecretSantaEventsByGroupsModalOpen = true,
                secretSantaEventsByGroup = events.filter { it.group == currentState.group?.id },
              )
            }
          }
          .onFailure { error ->
            viewModelState.update { state ->
              state.copy(
                isLoading = false,
                error = error,
              )
            }
          }
      }
    } else {
      viewModelState.update { state -> state.copy(isWishlistsByGroupsModalOpen = true) }
    }
  }

  /** Closes the shared-wishlists modal. */
  fun onCloseWishlistsByGroupModal() {
    viewModelState.update { state -> state.copy(isWishlistsByGroupsModalOpen = false) }
  }

  /** Closes the Secret Santa events modal. */
  fun onCloseSecretSantaEventsByGroupModal() {
    viewModelState.update { state -> state.copy(isSecretSantaEventsByGroupsModalOpen = false) }
  }

  /** Clears the currently displayed error dialog. */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /** Fetches the current group and updates the fullscreen state accordingly. */
  private suspend fun fetchGroup() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchGroupUseCase(groupId)
    viewModelState.update { state ->
      state.copy(
        group = result.getOrNull(),
        isLoadingFullscreen = false,
        error = result.exceptionOrNull()
      )
    }
  }

  private data class ViewModelState(
    val groupName: String,
    val group: Group.Detail? = null,
    val isWishlistsByGroupsModalOpen: Boolean = false,
    val isSecretSantaEventsByGroupsModalOpen: Boolean = false,
    val wishlistsByGroup: List<SharedWishlist> = emptyList(),
    val secretSantaEventsByGroup: List<SecretSantaEvent> = emptyList(),
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    fun toUiState(errorUiMapper: ErrorUiMapper) = when {
      isLoadingFullscreen ->
        GroupDetailUiState.Loading(groupName)

      group == null ->
        GroupDetailUiState.Error(groupName)

      else ->
        GroupDetailUiState.Detail(
          group = group,
          isWishlistsByGroupsModalOpen = isWishlistsByGroupsModalOpen,
          isSecretSantaEventsByGroupsModalOpen = isSecretSantaEventsByGroupsModalOpen,
          wishlistsByGroup = wishlistsByGroup,
          secretSantaEventsByGroup = secretSantaEventsByGroup,
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )
    }
  }
}

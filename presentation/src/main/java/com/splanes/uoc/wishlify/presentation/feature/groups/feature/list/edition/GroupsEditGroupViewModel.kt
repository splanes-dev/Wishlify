package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.edition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.FetchGroupUseCase
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.UpdateGroupUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserByIdUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.mapper.GroupsNewGroupFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.mapper.GroupsNewGroupFormMapper
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupForm
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupFormErrors
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.MembersNewGroupFormError
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.NameNewGroupFormError
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

/**
 * ViewModel that drives the group edition flow.
 *
 * It loads the selected group into the reusable group form, resolves users
 * returned by the search flow and submits the resulting update request.
 */
class GroupsEditGroupViewModel(
  private val groupId: String,
  groupName: String,
  private val fetchGroupUseCase: FetchGroupUseCase,
  private val fetchUserByIdUseCase: FetchUserByIdUseCase,
  private val updateGroupUseCase: UpdateGroupUseCase,
  private val formMapper: GroupsNewGroupFormMapper,
  private val formErrorsMapper: GroupsNewGroupFormErrorMapper,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState(groupName))

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchGroup() }
    .map { state ->
      state.toUiState(
        formErrorsMapper,
        errorUiMapper
      )
    }
    .stateIn(
      initialValue = viewModelState.value.toUiState(formErrorsMapper, errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<GroupsEditGroupUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  /** Validates and submits the edited group form. */
  fun onUpdateGroup(form: GroupsNewGroupForm) {
    if (validateForm(form)) {
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        val request = formMapper.requestOf(groupId, form, includeCurrentUser = true)
        updateGroupUseCase(request)
          .onSuccess {
            viewModelState.update { state -> state.copy(isLoading = false) }
            uiSideEffectChannel.send(GroupsEditGroupUiSideEffect.GroupUpdated)
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

  /** Persists the current in-progress form locally in the ViewModel state. */
  fun onSaveCurrentForm(form: GroupsNewGroupForm) {
    viewModelState.update { state -> state.copy(form = form) }
  }

  /** Resolves users returned by the search flow and adds them to the form. */
  fun onUserSearchResult(users: List<String>) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      coroutineScope {
        val result = users
          .map { uid -> async { fetchUserByIdUseCase(uid) } }
          .awaitAll()
          .mapNotNull { result -> result.getOrNull() }
        viewModelState.update { state ->
          state.copy(
            form = state.form.copy(
              members = (state.form.members + result).distinctBy { it.uid }
            ),
            isLoading = false
          )
        }
      }
    }
  }

  /** Clears the validation error associated with the edited input field. */
  fun onClearInputError(input: GroupsNewGroupForm.Input) {
    viewModelState.update { state ->
      when (input) {
        GroupsNewGroupForm.Input.Name ->
          state.copy(formErrors = state.formErrors.copy(nameError = null))

        GroupsNewGroupForm.Input.Members ->
          state.copy(formErrors = state.formErrors.copy(membersError = null))
      }
    }
  }

  /** Clears the currently displayed error dialog. */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /** Applies presentation-layer validation rules before group update. */
  fun validateForm(form: GroupsNewGroupForm): Boolean {
    val nameError = when {
      form.name.isBlank() -> NameNewGroupFormError.Blank
      form.name.count() !in 3..30 -> NameNewGroupFormError.Length
      else -> null
    }

    val membersError = when {
      form.members.count() !in 1..15 -> MembersNewGroupFormError.MembersCount
      else -> null
    }

    viewModelState.update { state ->
      state.copy(
        formErrors = state.formErrors.copy(
          nameError = nameError,
          membersError = membersError
        )
      )
    }

    return nameError == null && membersError == null
  }

  /** Fetches the current group and pre-fills the reusable form state. */
  private suspend fun fetchGroup() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchGroupUseCase(groupId)
    viewModelState.update { state ->
      state.copy(
        group = result.getOrNull(),
        form = result.getOrNull()?.let(formMapper::autofill) ?: state.form,
        isLoadingFullscreen = false,
      )
    }
  }

  private data class ViewModelState(
    val groupName: String,
    val group: Group? = null,
    val form: GroupsNewGroupForm = GroupsNewGroupForm(),
    val formErrors: GroupsNewGroupFormErrors = GroupsNewGroupFormErrors(),
    val isLoading: Boolean = false,
    val isLoadingFullscreen: Boolean = true,
    val error: Throwable? = null,
  ) {
    fun toUiState(
      formErrorsMapper: GroupsNewGroupFormErrorMapper,
      errorUiMapper: ErrorUiMapper
    ) = when {
      isLoadingFullscreen ->
        GroupsEditGroupUiState.Loading(groupName)

      group == null ->
        GroupsEditGroupUiState.Error(groupName)

      else ->
        GroupsEditGroupUiState.Form(
          group = group,
          form = form,
          formErrors = formErrorsMapper.map(formErrors),
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )
    }
  }
}

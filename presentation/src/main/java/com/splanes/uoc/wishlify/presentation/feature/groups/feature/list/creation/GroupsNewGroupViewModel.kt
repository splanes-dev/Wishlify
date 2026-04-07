package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.CreateGroupUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.User
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupsNewGroupViewModel(
  private val createGroupUseCase: CreateGroupUseCase,
  private val fetchUserByIdUseCase: FetchUserByIdUseCase,
  private val formMapper: GroupsNewGroupFormMapper,
  private val formErrorsMapper: GroupsNewGroupFormErrorMapper,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
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

  private val uiSideEffectChannel = Channel<GroupsNewGroupUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  fun onCreate(form: GroupsNewGroupForm) {
    if (validateForm(form)) {
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        val request = formMapper.requestOf(form)
        createGroupUseCase(request)
          .onSuccess {
            viewModelState.update { state -> state.copy(isLoading = false) }
            uiSideEffectChannel.send(GroupsNewGroupUiSideEffect.GroupCreated)
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

  fun onRemoveSelectedMember(user: User.Basic) {
    viewModelState.update { state ->
      state.copy(
        form = state.form.copy(members = state.form.members - user)
      )
    }
  }

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

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

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

  private data class ViewModelState(
    val form: GroupsNewGroupForm = GroupsNewGroupForm(),
    val formErrors: GroupsNewGroupFormErrors = GroupsNewGroupFormErrors(),
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    fun toUiState(
      formErrorsMapper: GroupsNewGroupFormErrorMapper,
      errorUiMapper: ErrorUiMapper
    ) =
      GroupsNewGroupUiState(
        form = form,
        formErrors = formErrorsMapper.map(formErrors),
        isLoading = isLoading,
        error = error?.let(errorUiMapper::map)
      )
  }
}
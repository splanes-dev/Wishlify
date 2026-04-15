package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.FetchGroupsUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.CreateSecretSantaEventUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.ValidateSecretSantaDrawUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserByIdUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.mapper.SecretSantaNewEventFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.mapper.SecretSantaNewEventFormMapper
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventBudgetFormError
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventDeadlineFormError
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventExclusionsFormError
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventForm
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventFormErrors
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventNameFormError
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventStep
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class SecretSantaNewEventViewModel(
  private val fetchGroupsUseCase: FetchGroupsUseCase,
  private val fetchUserByIdUseCase: FetchUserByIdUseCase,
  private val createSecretSantaEventUseCase: CreateSecretSantaEventUseCase,
  private val validateSecretSantaDrawUseCase: ValidateSecretSantaDrawUseCase,
  private val formMapper: SecretSantaNewEventFormMapper,
  private val formErrorMapper: SecretSantaNewEventFormErrorMapper,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())

  val uiState = viewModelState.asStateFlow()
    .map { state -> state.toUiState(formErrorMapper, errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(formErrorMapper, errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<SecretSantaNewEventUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  fun onNextStep() {
    viewModelState.update { state ->
      val nextStep = state.step.next()
      when (nextStep) {
        SecretSantaNewEventStep.Basics -> {
          // Nothing to do
        }
        SecretSantaNewEventStep.Participants -> fetchGroups()
        SecretSantaNewEventStep.Exclusions -> fetchGroupMembers()
      }
      state.copy(step = nextStep)
    }
  }

  fun onPrevStep() {
    viewModelState.update { state ->
      state.copy(step = state.step.previous())
    }
  }

  fun onNext(form: SecretSantaNewEventForm) {
    val currentStep = viewModelState.value.step
    val isValid = when (currentStep) {
      SecretSantaNewEventStep.Basics -> validateBasicsForm(form)
      SecretSantaNewEventStep.Participants -> true // No validation needed
      SecretSantaNewEventStep.Exclusions -> validateExclusionsForm(form)
    }

    viewModelState.update { state -> state.copy(form = form) }

    when {
      isValid && currentStep == SecretSantaNewEventStep.Exclusions -> onCreate(form)
      isValid -> onNextStep()
    }
  }

  fun onCreate(form: SecretSantaNewEventForm) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      val currentState = viewModelState.value
      val request = formMapper.requestOf(currentState.inviteLink, form)
      createSecretSantaEventUseCase(request)
        .onSuccess {
          viewModelState.update { state -> state.copy(isLoading = false) }
          uiSideEffectChannel.send(SecretSantaNewEventUiSideEffect.EventCreated)
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

  fun onNewGroupResult() {
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
              participants = (state.form.participants + result).distinctBy { it.uid },
            ),
            isLoading = false
          )
        }
      }
    }
  }

  fun onClearInputError(input: SecretSantaNewEventForm.Input) {
    viewModelState.update { state ->
      when (input) {
        SecretSantaNewEventForm.Input.Name ->
          state.copy(formErrors = state.formErrors.copy(name = null))

        SecretSantaNewEventForm.Input.Budget ->
          state.copy(formErrors = state.formErrors.copy(budget = null))

        SecretSantaNewEventForm.Input.Deadline ->
          state.copy(formErrors = state.formErrors.copy(deadline = null))

        SecretSantaNewEventForm.Input.Exclusions ->
          state.copy(formErrors = state.formErrors.copy(exclusions = null))
      }
    }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private fun fetchGroups() {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      val result = fetchGroupsUseCase()
      viewModelState.update { state ->
        state.copy(
          groups = result.getOrDefault(emptyList()),
          isLoading = false
        )
      }
    }
  }

  private fun fetchGroupMembers() {
    val currentState = viewModelState.value
    val group = currentState.form.group
    if (group != null) {
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        coroutineScope {
          val members = group.members
            .map { uid -> async { fetchUserByIdUseCase(uid) } }
            .awaitAll()
            .mapNotNull { result -> result.getOrNull() }

          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              allParticipants = currentState.form.participants + members
            )
          }
        }
      }
    }
  }

  private fun validateBasicsForm(form: SecretSantaNewEventForm): Boolean {
    val nameError = when {
      form.name.count() !in 3..30 -> SecretSantaNewEventNameFormError.Length
      else -> null
    }

    val budgetError = when {
      form.budget !in 1.0..100.0 -> SecretSantaNewEventBudgetFormError.Invalid
      else -> null
    }

    val deadlineError = when {
      !isValidDate(form.deadline) -> SecretSantaNewEventDeadlineFormError.Invalid
      else -> null
    }

    viewModelState.update { state ->
      state.copy(
        formErrors = state.formErrors.copy(
          name = nameError,
          budget = budgetError,
          deadline = deadlineError
        )
      )
    }

    return nameError == null && budgetError == null && deadlineError == null
  }

  private fun validateExclusionsForm(form: SecretSantaNewEventForm): Boolean {
    val currentState = viewModelState.value
    val isValid = validateSecretSantaDrawUseCase(
      participants = currentState.allParticipants,
      exclusions = form.exclusions
    )

    viewModelState.update { state ->
      state.copy(
        formErrors = state.formErrors.copy(
          exclusions = if (isValid) null else SecretSantaNewEventExclusionsFormError.Invalid
        )
      )
    }

    return isValid
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
    val step: SecretSantaNewEventStep = SecretSantaNewEventStep.Basics,
    val form: SecretSantaNewEventForm = SecretSantaNewEventForm(),
    val formErrors: SecretSantaNewEventFormErrors = SecretSantaNewEventFormErrors(),
    val groups: List<Group.Basic> = emptyList(),
    val allParticipants: List<User.Basic> = emptyList(),
    val inviteLink: InviteLink = InviteLink.new(InviteLink.SecretSanta),
    val isLoading: Boolean = false,
    val error: Throwable? = null,
  ) {
    fun toUiState(
      formErrorMapper: SecretSantaNewEventFormErrorMapper,
      errorUiMapper: ErrorUiMapper
    ) =
      SecretSantaNewEventUiState(
        step = step,
        form = form,
        formErrors = formErrorMapper.map(formErrors),
        isLoading = isLoading,
        groups = groups,
        allParticipants = allParticipants,
        inviteLink = inviteLink,
        error = error?.let(errorUiMapper::map)
      )
  }
}
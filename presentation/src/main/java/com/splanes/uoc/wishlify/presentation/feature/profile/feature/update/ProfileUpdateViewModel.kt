package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchBasicUserProfileUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.UpdateUserProfileUseCase
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.mapper.UserProfileUpdateFormErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.mapper.UserProfileUpdateFormMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateForm
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateFormEmailError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateFormErrors
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateFormNameError
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

class ProfileUpdateViewModel(
  private val fetchBasicUserProfileUseCase: FetchBasicUserProfileUseCase,
  private val updateUserProfileUseCase: UpdateUserProfileUseCase,
  private val formMapper: UserProfileUpdateFormMapper,
  private val formErrorsMapper: UserProfileUpdateFormErrorUiMapper,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())
  val uiState = viewModelState.asStateFlow()
    .onStart { fetchProfile() }
    .map { state -> state.toUiState(formErrorsMapper, errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(
        formErrorsMapper = formErrorsMapper,
        errorUiMapper = errorUiMapper,
      ),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<ProfileUpdateUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  fun onUpdate(form: UserProfileUpdateForm) {
    if (validateForm(form)) {
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        val currentState = viewModelState.value
        val user = currentState.user ?: error("User is null, this should not happen")
        val request = formMapper.map(user, form)
        updateUserProfileUseCase(request)
          .onSuccess {
            viewModelState.update { state -> state.copy(isLoading = false) }
            uiSideEffectChannel.send(ProfileUpdateUiSideEffect.ProfileUpdated)
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

  fun onClearInputError(input: UserProfileUpdateForm.Input) {
    viewModelState.update { state ->
      when (input) {
        UserProfileUpdateForm.Input.Username ->
          state.copy(formErrors = state.formErrors.copy(username = null))

        UserProfileUpdateForm.Input.Email ->
          state.copy(formErrors = state.formErrors.copy(email = null))
      }
    }
  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  private suspend fun fetchProfile() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchBasicUserProfileUseCase()
    viewModelState.update { state ->
      state.copy(
        user = result.getOrNull(),
        form = result.getOrNull()?.let { user ->
          state.form.copy(
            photo = user.photoUrl?.let(ImagePicker::Url),
            username = user.username,
            email = user.email
          )
        } ?: state.form,
        isSocialAccount = result.getOrNull()?.isSocialAccount == true,
        isLoadingFullscreen = false
      )
    }
  }

  private fun validateForm(form: UserProfileUpdateForm): Boolean {
    val usernameError = when {
      form.username.isBlank() -> UserProfileUpdateFormNameError.Blank
      form.username.count() !in 3..20 -> UserProfileUpdateFormNameError.Length
      !form.username.matches(UsernameRegex) -> UserProfileUpdateFormNameError.InvalidChars
      else -> null
    }

    val emailError = when {
      !form.email.matches(EmailRegex) -> UserProfileUpdateFormEmailError.Invalid
      else -> null
    }

    viewModelState.update { state ->
      state.copy(
        formErrors = state.formErrors.copy(
          username = usernameError,
          email = emailError
        )
      )
    }
    return emailError == null && usernameError == null
  }

  private data class ViewModelState(
    val user: User.BasicProfile? = null,
    val form: UserProfileUpdateForm = UserProfileUpdateForm(),
    val formErrors: UserProfileUpdateFormErrors = UserProfileUpdateFormErrors(),
    val isSocialAccount: Boolean = false,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    fun toUiState(
      formErrorsMapper: UserProfileUpdateFormErrorUiMapper,
      errorUiMapper: ErrorUiMapper
    ) = when {
      isLoadingFullscreen ->
        ProfileUpdateUiState.Loading

      user == null ->
        ProfileUpdateUiState.Error

      else ->
        ProfileUpdateUiState.Form(
          user = user,
          form = form,
          formErrors = formErrorsMapper.map(formErrors),
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map)
        )
    }
  }
}

private val UsernameRegex = Regex("^[a-zA-Z0-9](?:[a-zA-Z0-9._ ]{1,18}[a-zA-Z0-9])?$")
private val EmailRegex = Regex("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
    "\\@" +
    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
    "(" +
    "\\." +
    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
    ")+")
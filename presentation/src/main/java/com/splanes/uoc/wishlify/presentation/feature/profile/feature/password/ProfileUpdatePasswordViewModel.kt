package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchBasicUserProfileUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.UpdateUserPasswordUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.mapper.UserProfileUpdatePasswordFormErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.mapper.UserProfileUpdatePasswordFormMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdateNewPasswordConfirmFormPasswordError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdateNewPasswordFormPasswordError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordForm
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordFormErrors
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordFormPasswordError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.ProfileUpdateUiSideEffect
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
 * Coordinates the password update flow, including local validation and profile loading.
 */
class ProfileUpdatePasswordViewModel(
  private val fetchBasicUserProfileUseCase: FetchBasicUserProfileUseCase,
  private val updateUserPasswordUseCase: UpdateUserPasswordUseCase,
  private val formMapper: UserProfileUpdatePasswordFormMapper,
  private val formErrorsMapper: UserProfileUpdatePasswordFormErrorUiMapper,
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

  /**
   * Validates the form and updates the current user password.
   */
  fun onUpdatePassword(form: UserProfileUpdatePasswordForm) {
    if (validateForm(form)) {
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        val request = formMapper.requestOf(form)
        updateUserPasswordUseCase(request)
          .onSuccess {
            viewModelState.update { state -> state.copy(isLoading = false) }
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
    }
  }

  /**
   * Clears the current UI error.
   */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /**
   * Clears the validation error associated with a specific form input.
   */
  fun onClearInputError(input: UserProfileUpdatePasswordForm.Input) {
    viewModelState.update { state ->
      when (input) {
        UserProfileUpdatePasswordForm.Input.CurrentPassword ->
          state.copy(formErrors = state.formErrors.copy(currentPassword = null))

        UserProfileUpdatePasswordForm.Input.NewPassword ->
          state.copy(formErrors = state.formErrors.copy(newPassword = null))

        UserProfileUpdatePasswordForm.Input.NewPasswordConfirm ->
          state.copy(formErrors = state.formErrors.copy(newPasswordConfirm = null))
      }
    }
  }

  /**
   * Loads the current basic profile required by the password update screen.
   */
  private suspend fun fetchProfile() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchBasicUserProfileUseCase()
    viewModelState.update { state ->
      state.copy(
        user = result.getOrNull(),
        isLoadingFullscreen = false,
      )
    }
  }

  /**
   * Validates the password update form.
   */
  private fun validateForm(form: UserProfileUpdatePasswordForm): Boolean {

    val passwordError = when {
      form.currentPassword.isBlank() -> UserProfileUpdatePasswordFormPasswordError.Blank
      else -> null
    }

    val newPasswordError = when {
      form.newPassword.isBlank() -> UserProfileUpdateNewPasswordFormPasswordError.Blank
      form.newPassword == form.currentPassword ||
          !form.newPassword.matches(PasswordRegex) -> UserProfileUpdateNewPasswordFormPasswordError.Weak
      else -> null
    }

    val newPasswordConfirmError = when {
      form.newPasswordConfirm.isBlank() -> UserProfileUpdateNewPasswordConfirmFormPasswordError.Blank
      form.newPasswordConfirm != form.newPassword -> UserProfileUpdateNewPasswordConfirmFormPasswordError.NotMatch
      else -> null
    }

    viewModelState.update { state ->
      state.copy(
        formErrors = state.formErrors.copy(
          currentPassword = passwordError,
          newPassword = newPasswordError,
          newPasswordConfirm = newPasswordConfirmError
        )
      )
    }

    return passwordError == null && newPasswordError == null && newPasswordConfirmError == null
  }

  private data class ViewModelState(
    val user: User.BasicProfile? = null,
    val form: UserProfileUpdatePasswordForm = UserProfileUpdatePasswordForm(),
    val formErrors: UserProfileUpdatePasswordFormErrors = UserProfileUpdatePasswordFormErrors(),
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    /**
     * Maps internal state to the password update UI contract.
     */
    fun toUiState(
      formErrorsMapper: UserProfileUpdatePasswordFormErrorUiMapper,
      errorUiMapper: ErrorUiMapper
    ) = when {
      isLoadingFullscreen -> ProfileUpdatePasswordUiState.Loading
      user == null -> ProfileUpdatePasswordUiState.Error
      else -> ProfileUpdatePasswordUiState.Form(
        user = user,
        form = form,
        formErrors = formErrorsMapper.map(formErrors),
        isLoading = isLoading,
        error = error?.let(errorUiMapper::map),
      )
    }
  }
}

/**
 * Password strength validation pattern required by the password update form.
 */
private val PasswordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$")

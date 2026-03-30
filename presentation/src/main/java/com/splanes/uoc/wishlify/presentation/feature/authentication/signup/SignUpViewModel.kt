package com.splanes.uoc.wishlify.presentation.feature.authentication.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SignUpRequest
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignUpUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.mapper.SignUpFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.EmailSignUpFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.PasswordSignUpFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.SignUpForm
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.UsernameSignUpFormError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
  private val signUpUseCase: SignUpUseCase,
  private val signUpFormErrorMapper: SignUpFormErrorMapper,
  private val errorUiMapper: ErrorUiMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())
  val uiState = viewModelState.asStateFlow()
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<SignUpUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  fun onSignUp(form: SignUpForm) {
    if (validateForm(form)) {
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        val request = SignUpRequest(
          username = form.username,
          email = form.email,
          password = form.password
        )

        signUpUseCase(request)
          .onSuccess {
            uiSideEffectChannel.send(SignUpUiSideEffect.NavToHome)
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

  fun onSocialSignUp() {

  }

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  fun onClearInputError(input: SignUpForm.Input) {
    viewModelState.update { state ->
      when (input) {
        SignUpForm.Input.Email -> state.copy(emailInputError = null)
        SignUpForm.Input.Username -> state.copy(usernameInputError = null)
        SignUpForm.Input.Password -> state.copy(passwordInputError = null)
      }
    }
  }

  private fun validateForm(form: SignUpForm): Boolean {
    val usernameError = when {
      form.username.isBlank() -> UsernameSignUpFormError.Blank
      form.username.count() !in 3..20 -> UsernameSignUpFormError.Length
      form.username.matches(UsernameRegex) -> UsernameSignUpFormError.InvalidChars
      else -> null
    }

    val emailError = when {
      !form.email.matches(Patterns.EMAIL_ADDRESS.toRegex()) -> EmailSignUpFormError.Invalid
      else -> null
    }

    val passwordError = when {
      !form.password.matches(PasswordRegex) -> PasswordSignUpFormError.Weak
      else -> null
    }

    viewModelState.update { state ->
      state.copy(
        emailInputError = emailError?.let(signUpFormErrorMapper::map),
        usernameInputError = usernameError?.let(signUpFormErrorMapper::map),
        passwordInputError = passwordError?.let(signUpFormErrorMapper::map)
      )
    }
    return emailError == null && usernameError == null && passwordError == null
  }

  private data class ViewModelState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val emailInputError: String? = null,
    val usernameInputError: String? = null,
    val passwordInputError: String? = null,
  ) {
    fun toUiState(errorUiMapper: ErrorUiMapper) = SignUpUiState.SignUpForm(
      isLoading = isLoading,
      error = error?.let(errorUiMapper::map),
      emailInputError = emailInputError,
      usernameInputError = usernameInputError,
      passwordInputError = passwordInputError
    )
  }
}

private val UsernameRegex = Regex("^[a-zA-Z0-9](?:[a-zA-Z0-9._]{1,18}[a-zA-Z0-9])?\\$")
private val PasswordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$")
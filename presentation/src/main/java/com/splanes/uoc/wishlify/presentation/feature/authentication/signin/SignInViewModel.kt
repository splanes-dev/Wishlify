package com.splanes.uoc.wishlify.presentation.feature.authentication.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SignInRequest
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.AutoSignInUseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.GoogleSignInUseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignInUseCase
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.mapper.SignInErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.mapper.SignInFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model.EmailSignInFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model.PasswordSignInFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model.SignInForm
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

class SignInViewModel(
  private val autoSignInUseCase: AutoSignInUseCase,
  private val signInUseCase: SignInUseCase,
  private val googleSignInUseCase: GoogleSignInUseCase,
  private val signInFormErrorMapper: SignInFormErrorMapper,
  private val errorUiMapper: SignInErrorMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())
  val uiState = viewModelState.asStateFlow()
    .onStart { tryAutoSignIn() }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<SignInUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  fun onSignIn(form: SignInForm) {
    if (validateForm(form)) {
      viewModelState.update { state -> state.copy(isLoading = true) }
      viewModelScope.launch {
        val request = SignInRequest(
          email = form.email,
          password = form.password
        )

        signInUseCase(request)
          .onSuccess {
            uiSideEffectChannel.send(SignInUiSideEffect.NavToHome)
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

  fun onGoogleSignIn() {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      googleSignInUseCase()
        .onSuccess {
          uiSideEffectChannel.send(SignInUiSideEffect.NavToHome)
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

  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  fun onClearInputError(input: SignInForm.Input) {
    viewModelState.update { state ->
      when (input) {
        SignInForm.Input.Email -> state.copy(emailInputError = null)
        SignInForm.Input.Password -> state.copy(passwordInputError = null)
      }
    }
  }

  private suspend fun tryAutoSignIn() {
    autoSignInUseCase()
      .onSuccess {
        uiSideEffectChannel.send(SignInUiSideEffect.NavToHome)
      }
      .onFailure {
        viewModelState.update { state ->
          state.copy(isAutoSignInLoading = false)
        }
      }
  }

  private fun validateForm(form: SignInForm): Boolean {
    val emailError = when {
      !form.email.matches(EmailRegex) -> EmailSignInFormError.Invalid
      else -> null
    }

    val passwordError = when {
      form.password.trim().count() == 0 -> PasswordSignInFormError.Blank
      else -> null
    }

    viewModelState.update { state ->
      state.copy(
        emailInputError = emailError?.let(signInFormErrorMapper::map),
        passwordInputError = passwordError?.let(signInFormErrorMapper::map)
      )
    }
    return emailError == null && passwordError == null
  }

  private data class ViewModelState(
    val isAutoSignInLoading: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val emailInputError: String? = null,
    val passwordInputError: String? = null,
  ) {
    fun toUiState(errorUiMapper: SignInErrorMapper): SignInUiState = when {
      isAutoSignInLoading ->
        SignInUiState.AutoSignIn

      else ->
        SignInUiState.SignInForm(
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map),
          emailInputError = emailInputError,
          passwordInputError = passwordInputError,
        )
    }
  }
}

private val EmailRegex = Regex("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
    "\\@" +
    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
    "(" +
    "\\." +
    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
    ")+")
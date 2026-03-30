package com.splanes.uoc.wishlify.presentation.feature.authentication.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class SignInViewModel(

) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState())
  val uiState = viewModelState.asStateFlow()
    .onStart { tryAutoSignIn() }
    .map { state -> state.toUiState() }
    .stateIn(
      initialValue = viewModelState.value.toUiState(),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private suspend fun tryAutoSignIn() {

  }

  private data class ViewModelState(
    val isAutoSignInLoading: Boolean = false,
    val isLoading: Boolean = false,
    val error: Any? = null
  ) {
    fun toUiState(): SignInUiState = when {
      isAutoSignInLoading ->
        SignInUiState.AutoSignIn

      else ->
        SignInUiState.SignInForm(
          isLoading = isLoading,
          error = error
        )
    }
  }
}
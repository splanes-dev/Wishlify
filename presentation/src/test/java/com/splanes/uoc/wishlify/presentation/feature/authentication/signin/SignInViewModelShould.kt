package com.splanes.uoc.wishlify.presentation.feature.authentication.signin

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SignInRequest
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.AutoSignInUseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.GoogleSignInUseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignInUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.mapper.SignInErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.mapper.SignInFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model.EmailSignInFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model.PasswordSignInFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model.SignInForm
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SignInViewModelShould : UnitTest() {

  private val autoSignInUseCase: AutoSignInUseCase = mock()
  private val signInUseCase: SignInUseCase = mock()
  private val googleSignInUseCase: GoogleSignInUseCase = mock()
  private val signUpFormErrorMapper: SignInFormErrorMapper = mock()
  private val errorUiMapper: SignInErrorMapper = mock()
  private lateinit var viewModel: SignInViewModel

  @Before
  fun setup() {
    viewModel = SignInViewModel(
      autoSignInUseCase = autoSignInUseCase,
      signInUseCase = signInUseCase,
      googleSignInUseCase = googleSignInUseCase,
      signInFormErrorMapper = signUpFormErrorMapper,
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `try autologin, when initializing, and do nothing if no credentials stored`() = runTest {
    whenever(autoSignInUseCase()).thenReturn(Result.failure(RuntimeException()))

    viewModel.uiState.test {
      var uiState = awaitItem()
      assertThat(uiState).isEqualTo(SignInUiState.AutoSignIn)

      uiState = awaitItem()
      assertThat(uiState).isInstanceOf(SignInUiState.SignInForm::class.java)
    }
  }

  @Test
  fun `try autologin, when initializing, and redirect if success`() = runTest {
    whenever(autoSignInUseCase()).thenReturn(Result.success(Unit))

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      val uiState = uiStateTurbine.awaitItem()
      assertThat(uiState).isEqualTo(SignInUiState.AutoSignIn)

      val effect = sideEffectTurbine.awaitItem()
      assertThat(effect).isEqualTo(SignInUiSideEffect.NavToHome)

      uiStateTurbine.cancel()
      sideEffectTurbine.cancel()
    }
  }

  @Test
  fun `sign in and redirect when form is valid and use case succeeds`() = runTest {
    whenever(autoSignInUseCase()).thenReturn(Result.failure(RuntimeException()))
    whenever(signInUseCase(any())).thenReturn(Result.success(Unit))

    val form = SignInForm(
      email = "test@test.com",
      password = "123456"
    )

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      uiStateTurbine.awaitItem()
      uiStateTurbine.awaitItem()

      viewModel.onSignIn(form)

      val loadingState = uiStateTurbine.awaitItem()
      assertThat(loadingState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = true,
          error = null,
          emailInputError = null,
          passwordInputError = null,
        )
      )

      val effect = sideEffectTurbine.awaitItem()
      assertThat(effect).isEqualTo(SignInUiSideEffect.NavToHome)

      verify(signInUseCase).invoke(
        SignInRequest(
          email = "test@test.com",
          password = "123456"
        )
      )

      uiStateTurbine.cancel()
      sideEffectTurbine.cancel()
    }
  }

  @Test
  fun `show error when sign in fails`() = runTest {
    val error = RuntimeException()
    whenever(autoSignInUseCase()).thenReturn(Result.failure(RuntimeException()))
    whenever(signInUseCase(any())).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    val form = SignInForm(
      email = "test@test.com",
      password = "123456"
    )

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onSignIn(form)

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = true,
          error = null,
          emailInputError = null,
          passwordInputError = null,
        )
      )

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = false,
          error = errorUiModel(),
          emailInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `show email input error when sign in form email is invalid`() = runTest {
    whenever(autoSignInUseCase()).thenReturn(Result.failure(RuntimeException()))
    whenever(signUpFormErrorMapper.map(EmailSignInFormError.Invalid)).thenReturn("invalid_email")

    val form = SignInForm(
      email = "bad_email",
      password = "123456"
    )

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onSignIn(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = false,
          error = null,
          emailInputError = "invalid_email",
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `show password input error when sign in form password is blank`() = runTest {
    whenever(autoSignInUseCase()).thenReturn(Result.failure(RuntimeException()))
    whenever(signUpFormErrorMapper.map(PasswordSignInFormError.Blank)).thenReturn("blank_password")

    val form = SignInForm(
      email = "test@test.com",
      password = "   "
    )

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onSignIn(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          passwordInputError = "blank_password",
        )
      )
    }
  }

  @Test
  fun `redirect when google sign in succeeds`() = runTest {
    whenever(autoSignInUseCase()).thenReturn(Result.failure(RuntimeException()))
    whenever(googleSignInUseCase()).thenReturn(Result.success(Unit))

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      uiStateTurbine.awaitItem()
      uiStateTurbine.awaitItem()

      viewModel.onGoogleSignIn()

      val loadingState = uiStateTurbine.awaitItem()
      assertThat(loadingState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = true,
          error = null,
          emailInputError = null,
          passwordInputError = null,
        )
      )

      val effect = sideEffectTurbine.awaitItem()
      assertThat(effect).isEqualTo(SignInUiSideEffect.NavToHome)

      uiStateTurbine.cancel()
      sideEffectTurbine.cancel()
    }
  }

  @Test
  fun `show error when google sign in fails`() = runTest {
    val error = RuntimeException()
    whenever(autoSignInUseCase()).thenReturn(Result.failure(RuntimeException()))
    whenever(googleSignInUseCase()).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onGoogleSignIn()

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = true,
          error = null,
          emailInputError = null,
          passwordInputError = null,
        )
      )

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = false,
          error = errorUiModel(),
          emailInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val error = RuntimeException()
    whenever(autoSignInUseCase()).thenReturn(Result.failure(RuntimeException()))
    whenever(googleSignInUseCase()).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onGoogleSignIn()
      awaitItem()
      awaitItem()

      viewModel.onDismissError()

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `clear email input error`() = runTest {
    whenever(autoSignInUseCase()).thenReturn(Result.failure(RuntimeException()))
    whenever(signUpFormErrorMapper.map(EmailSignInFormError.Invalid)).thenReturn("invalid_email")

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onSignIn(
        SignInForm(
          email = "bad_email",
          password = "123456"
        )
      )
      awaitItem()

      viewModel.onClearInputError(SignInForm.Input.Email)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `clear password input error`() = runTest {
    whenever(autoSignInUseCase()).thenReturn(Result.failure(RuntimeException()))
    whenever(signUpFormErrorMapper.map(PasswordSignInFormError.Blank)).thenReturn("blank_password")

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onSignIn(
        SignInForm(
          email = "test@test.com",
          password = " "
        )
      )
      awaitItem()

      viewModel.onClearInputError(SignInForm.Input.Password)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignInUiState.SignInForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          passwordInputError = null,
        )
      )
    }
  }
}
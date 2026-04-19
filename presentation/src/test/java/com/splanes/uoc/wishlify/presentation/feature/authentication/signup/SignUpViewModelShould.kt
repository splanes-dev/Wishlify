package com.splanes.uoc.wishlify.presentation.feature.authentication.signup

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SignUpRequest
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.GoogleSignUpUseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignUpUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.mapper.SignUpErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.mapper.SignUpFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.EmailSignUpFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.PasswordSignUpFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.SignUpForm
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.UsernameSignUpFormError
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SignUpViewModelShould : UnitTest() {

  private val signUpUseCase: SignUpUseCase = mock()
  private val googleSignUpUseCase: GoogleSignUpUseCase = mock()
  private val signUpFormErrorMapper: SignUpFormErrorMapper = mock()
  private val errorUiMapper: SignUpErrorMapper = mock()
  private lateinit var viewModel: SignUpViewModel

  @Before
  fun setup() {
    viewModel = SignUpViewModel(
      signUpUseCase = signUpUseCase,
      googleSignUpUseCase = googleSignUpUseCase,
      signUpFormErrorMapper = signUpFormErrorMapper,
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `have initial sign up form state`() = runTest {
    viewModel.uiState.test {
      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          usernameInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `sign up and redirect when form is valid and use case succeeds`() = runTest {
    whenever(signUpUseCase(any())).thenReturn(Result.success(Unit))

    val form = SignUpForm(
      username = "validUser",
      email = "test@test.com",
      password = "Aa123456!"
    )

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      uiStateTurbine.awaitItem()

      viewModel.onSignUp(form)

      val loadingState = uiStateTurbine.awaitItem()
      assertThat(loadingState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = true,
          error = null,
          emailInputError = null,
          usernameInputError = null,
          passwordInputError = null,
        )
      )

      val effect = sideEffectTurbine.awaitItem()
      assertThat(effect).isEqualTo(SignUpUiSideEffect.NavToHome)

      verify(signUpUseCase).invoke(
        SignUpRequest(
          username = "validUser",
          email = "test@test.com",
          password = "Aa123456!"
        )
      )

      uiStateTurbine.cancel()
      sideEffectTurbine.cancel()
    }
  }

  @Test
  fun `show error when sign up fails`() = runTest {
    val error = RuntimeException()
    whenever(signUpUseCase(any())).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    val form = SignUpForm(
      username = "validUser",
      email = "test@test.com",
      password = "Aa123456!"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSignUp(form)

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = errorUiModel(),
          emailInputError = null,
          usernameInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `show username blank error when username is blank`() = runTest {
    whenever(signUpFormErrorMapper.map(UsernameSignUpFormError.Blank))
      .thenReturn("blank_username")

    val form = SignUpForm(
      username = "",
      email = "test@test.com",
      password = "Aa123456!"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSignUp(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          usernameInputError = "blank_username",
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `show username length error when username length is invalid`() = runTest {
    whenever(signUpFormErrorMapper.map(UsernameSignUpFormError.Length))
      .thenReturn("invalid_length")

    val form = SignUpForm(
      username = "ab",
      email = "test@test.com",
      password = "Aa123456!"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSignUp(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          usernameInputError = "invalid_length",
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `show username invalid chars error when username has invalid chars`() = runTest {
    whenever(signUpFormErrorMapper.map(UsernameSignUpFormError.InvalidChars))
      .thenReturn("invalid_chars")

    val form = SignUpForm(
      username = "!?=34455",
      email = "test@test.com",
      password = "Aa123456!"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSignUp(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          usernameInputError = "invalid_chars",
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `show email input error when email is invalid`() = runTest {
    whenever(signUpFormErrorMapper.map(EmailSignUpFormError.Invalid))
      .thenReturn("invalid_email")

    val form = SignUpForm(
      username = "validUser",
      email = "bad_email",
      password = "Aa123456!"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSignUp(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = "invalid_email",
          usernameInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `show password weak error when password is invalid`() = runTest {
    whenever(signUpFormErrorMapper.map(PasswordSignUpFormError.Weak))
      .thenReturn("weak_password")

    val form = SignUpForm(
      username = "validUser",
      email = "test@test.com",
      password = "1234"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSignUp(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          usernameInputError = null,
          passwordInputError = "weak_password",
        )
      )
    }
  }

  @Test
  fun `show multiple input errors when form has multiple invalid fields`() = runTest {
    whenever(signUpFormErrorMapper.map(UsernameSignUpFormError.Blank))
      .thenReturn("blank_username")
    whenever(signUpFormErrorMapper.map(EmailSignUpFormError.Invalid))
      .thenReturn("invalid_email")
    whenever(signUpFormErrorMapper.map(PasswordSignUpFormError.Weak))
      .thenReturn("weak_password")

    val form = SignUpForm(
      username = "",
      email = "bad_email",
      password = "1234"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSignUp(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = "invalid_email",
          usernameInputError = "blank_username",
          passwordInputError = "weak_password",
        )
      )
    }
  }

  @Test
  fun `redirect when google sign up succeeds`() = runTest {
    whenever(googleSignUpUseCase()).thenReturn(Result.success(Unit))

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      uiStateTurbine.awaitItem()

      viewModel.onGoogleSignUp()

      val loadingState = uiStateTurbine.awaitItem()

      assertThat(loadingState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = true,
          error = null,
          emailInputError = null,
          usernameInputError = null,
          passwordInputError = null,
        )
      )

      val effect = sideEffectTurbine.awaitItem()
      assertThat(effect).isEqualTo(SignUpUiSideEffect.NavToHome)

      uiStateTurbine.cancel()
      sideEffectTurbine.cancel()
    }
  }

  @Test
  fun `show error when google sign up fails`() = runTest {
    val error = RuntimeException()
    whenever(googleSignUpUseCase()).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()

      viewModel.onGoogleSignUp()

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = errorUiModel(),
          emailInputError = null,
          usernameInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val error = RuntimeException()
    whenever(googleSignUpUseCase()).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()

      viewModel.onGoogleSignUp()
      awaitItem()

      viewModel.onDismissError()

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          usernameInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `clear email input error`() = runTest {
    whenever(signUpFormErrorMapper.map(EmailSignUpFormError.Invalid))
      .thenReturn("invalid_email")

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSignUp(
        SignUpForm(
          username = "validUser",
          email = "bad_email",
          password = "Aa123456!"
        )
      )
      awaitItem()

      viewModel.onClearInputError(SignUpForm.Input.Email)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          usernameInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `clear username input error`() = runTest {
    whenever(signUpFormErrorMapper.map(UsernameSignUpFormError.Blank))
      .thenReturn("blank_username")

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSignUp(
        SignUpForm(
          username = "",
          email = "test@test.com",
          password = "Aa123456!"
        )
      )
      awaitItem()

      viewModel.onClearInputError(SignUpForm.Input.Username)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          usernameInputError = null,
          passwordInputError = null,
        )
      )
    }
  }

  @Test
  fun `clear password input error`() = runTest {
    whenever(signUpFormErrorMapper.map(PasswordSignUpFormError.Weak))
      .thenReturn("weak_password")

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSignUp(
        SignUpForm(
          username = "validUser",
          email = "test@test.com",
          password = "1234"
        )
      )
      awaitItem()

      viewModel.onClearInputError(SignUpForm.Input.Password)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        SignUpUiState.SignUpForm(
          isLoading = false,
          error = null,
          emailInputError = null,
          usernameInputError = null,
          passwordInputError = null,
        )
      )
    }
  }
}
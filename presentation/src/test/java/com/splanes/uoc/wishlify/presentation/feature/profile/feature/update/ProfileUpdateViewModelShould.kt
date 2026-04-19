package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchBasicUserProfileUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.UpdateUserProfileUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.mapper.UserProfileUpdateFormErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.mapper.UserProfileUpdateFormMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateForm
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateFormUiErrors
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileUpdateViewModelShould : UnitTest() {

  private val fetchBasicUserProfileUseCase: FetchBasicUserProfileUseCase = mock()
  private val updateUserProfileUseCase: UpdateUserProfileUseCase = mock()
  private val formMapper: UserProfileUpdateFormMapper = mock()
  private val formErrorsMapper: UserProfileUpdateFormErrorUiMapper = mock {
    on { map(any()) } doReturn UserProfileUpdateFormUiErrors(null, null)
  }
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: ProfileUpdateViewModel

  @Before
  fun setup() {
    viewModel = ProfileUpdateViewModel(
      fetchBasicUserProfileUseCase = fetchBasicUserProfileUseCase,
      updateUserProfileUseCase = updateUserProfileUseCase,
      formMapper = formMapper,
      formErrorsMapper = formErrorsMapper,
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `fetch profile when initializing and show form when success`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(ProfileUpdateUiState.Loading)

      val formState = awaitItem()
      assertThat(formState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(null, null),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show error when profile fetch fails`() = runTest {
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.failure(RuntimeException()))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(ProfileUpdateUiState.Loading)

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(ProfileUpdateUiState.Error)
    }
  }

  @Test
  fun `update profile when form is valid and use case succeeds`() = runTest {
    val user = basicProfileUser()
    val form = UserProfileUpdateForm(
      photo = ImagePicker.Url("new_photo"),
      username = "validUser",
      email = "valid@test.com"
    )
    val request = mock<com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest>()

    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))
    whenever(formMapper.map(user, form)).thenReturn(request)
    whenever(updateUserProfileUseCase(request)).thenReturn(Result.success(Unit))

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      uiStateTurbine.awaitItem()
      uiStateTurbine.awaitItem()

      viewModel.onUpdate(form)

      val loadingState = uiStateTurbine.awaitItem()
      assertThat(loadingState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(null, null),
          isLoading = true,
          error = null,
        )
      )

      val finalState = uiStateTurbine.awaitItem()
      assertThat(finalState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(null, null),
          isLoading = false,
          error = null,
        )
      )

      val effect = sideEffectTurbine.awaitItem()
      assertThat(effect).isEqualTo(ProfileUpdateUiSideEffect.ProfileUpdated)

      verify(formMapper).map(user, form)
      verify(updateUserProfileUseCase).invoke(request)

      uiStateTurbine.cancelAndIgnoreRemainingEvents()
      sideEffectTurbine.cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `show error when update profile fails`() = runTest {
    val user = basicProfileUser()
    val form = UserProfileUpdateForm(
      photo = ImagePicker.Url("new_photo"),
      username = "validUser",
      email = "valid@test.com"
    )
    val request = mock<com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest>()
    val error = RuntimeException()

    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))
    whenever(formMapper.map(user, form)).thenReturn(request)
    whenever(updateUserProfileUseCase(request)).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onUpdate(form)

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(null, null),
          isLoading = true,
          error = null,
        )
      )

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(null, null),
          isLoading = false,
          error = errorUiModel(),
        )
      )
    }
  }

  @Test
  fun `show username blank error when username is blank`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    mockFormErrorMapper(usernameError = "blank")

    val form = UserProfileUpdateForm(
      photo = null,
      username = "",
      email = "valid@test.com"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onUpdate(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(
            usernameError = "blank",
            emailError = null,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show username length error when username length is invalid`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    mockFormErrorMapper(usernameError = "length")

    val form = UserProfileUpdateForm(
      photo = null,
      username = "ab",
      email = "valid@test.com"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onUpdate(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(
            usernameError = "length",
            emailError = null,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show username invalid chars error when username has invalid chars`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    mockFormErrorMapper(usernameError = "invalid")

    val form = UserProfileUpdateForm(
      photo = null,
      username = "invalid user",
      email = "valid@test.com"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onUpdate(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(
            usernameError = "invalid",
            emailError = null,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show email invalid error when email is invalid`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    mockFormErrorMapper(emailError = "invalid")

    val form = UserProfileUpdateForm(
      photo = null,
      username = "validUser",
      email = "bad_email"
    )

    viewModel.uiState.test {
      awaitItem()

      viewModel.onUpdate(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(
            usernameError = null,
            emailError = "invalid",
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `clear username input error`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    viewModel.uiState.test {

      viewModel.onUpdate(
        UserProfileUpdateForm(
          photo = null,
          username = "",
          email = "valid@test.com"
        )
      )
      awaitItem()

      viewModel.onClearInputError(UserProfileUpdateForm.Input.Username)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(
            usernameError = null,
            emailError = null,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `clear email input error`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    viewModel.uiState.test {

      viewModel.onUpdate(
        UserProfileUpdateForm(
          photo = null,
          username = "validUser",
          email = "bad_email"
        )
      )
      awaitItem()

      viewModel.onClearInputError(UserProfileUpdateForm.Input.Email)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(
            usernameError = null,
            emailError = null,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val user = basicProfileUser()
    val form = UserProfileUpdateForm(
      photo = ImagePicker.Url("new_photo"),
      username = "validUser",
      email = "valid@test.com"
    )
    val request = mock<com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest>()
    val error = RuntimeException()

    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))
    whenever(formMapper.map(user, form)).thenReturn(request)
    whenever(updateUserProfileUseCase(request)).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onUpdate(form)
      awaitItem()
      awaitItem()

      viewModel.onDismissError()

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdateUiState.Form(
          user = user,
          form = UserProfileUpdateForm(
            photo = ImagePicker.Url("photo"),
            username = "sergi",
            email = "sergi@test.com"
          ),
          formErrors = UserProfileUpdateFormUiErrors(null, null),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  private fun mockFormErrorMapper(
    usernameError: String? = null,
    emailError: String? = null,
  ) {
    whenever { formErrorsMapper.map(any()) } doReturn UserProfileUpdateFormUiErrors(
      usernameError = usernameError,
      emailError = emailError,
    )
  }

  private fun basicProfileUser() = User.BasicProfile(
    uid = "uid",
    username = "sergi",
    email = "sergi@test.com",
    photoUrl = "photo",
    code = "",
    points = 0,
    isSocialAccount = false,
  )
}
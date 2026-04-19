package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdatePasswordRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchBasicUserProfileUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.UpdateUserPasswordUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.mapper.UserProfileUpdatePasswordFormErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.mapper.UserProfileUpdatePasswordFormMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordForm
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordFormUiErrors
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileUpdatePasswordViewModelShould : UnitTest() {

  private val fetchBasicUserProfileUseCase: FetchBasicUserProfileUseCase = mock()
  private val updateUserPasswordUseCase: UpdateUserPasswordUseCase = mock()
  private val formMapper: UserProfileUpdatePasswordFormMapper = mock()
  private val formErrorsMapper: UserProfileUpdatePasswordFormErrorUiMapper = mock {
    on { map(any()) } doReturn UserProfileUpdatePasswordFormUiErrors(
      currentPassword = null,
      newPassword = null,
      newPasswordConfirm = null
    )
  }
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: ProfileUpdatePasswordViewModel

  @Before
  fun setup() {
    viewModel = ProfileUpdatePasswordViewModel(
      fetchBasicUserProfileUseCase = fetchBasicUserProfileUseCase,
      updateUserPasswordUseCase = updateUserPasswordUseCase,
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
      assertThat(loadingState).isEqualTo(ProfileUpdatePasswordUiState.Loading)

      val formState = awaitItem()
      assertThat(formState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            null,
            null,
            null
          ),
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
      assertThat(loadingState).isEqualTo(ProfileUpdatePasswordUiState.Loading)

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(ProfileUpdatePasswordUiState.Error)
    }
  }

  @Test
  fun `show current password blank error when current password is blank`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    mockFormErrorMapper(currentPassword = "error")

    val form = UserProfileUpdatePasswordForm(
      currentPassword = "",
      newPassword = "New1234!",
      newPasswordConfirm = "New1234!"
    )

    viewModel.uiState.test {

      awaitItem()

      viewModel.onUpdatePassword(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            currentPassword = "error",
            newPassword = null,
            newPasswordConfirm = null,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `update password when form is valid and use case succeeds`() = runTest {
    val user = basicProfileUser()
    val form = UserProfileUpdatePasswordForm(
      currentPassword = "Old1234!",
      newPassword = "New1234!",
      newPasswordConfirm = "New1234!"
    )
    val request = mock<UpdatePasswordRequest>()

    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))
    whenever(formMapper.requestOf(form)).thenReturn(request)
    whenever(updateUserPasswordUseCase(request)).thenReturn(Result.success(Unit))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onUpdatePassword(form)

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            null,
            null,
            null,
          ),
          isLoading = true,
          error = null,
        )
      )

      val finalState = awaitItem()
      assertThat(finalState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            null,
            null,
            null
          ),
          isLoading = false,
          error = null,
        )
      )

      verify(formMapper).requestOf(form)
      verify(updateUserPasswordUseCase).invoke(request)
    }
  }

  @Test
  fun `show error when update password fails`() = runTest {
    val user = basicProfileUser()
    val form = UserProfileUpdatePasswordForm(
      currentPassword = "Old1234!",
      newPassword = "New1234!",
      newPasswordConfirm = "New1234!"
    )
    val request = mock<UpdatePasswordRequest>()
    val error = RuntimeException()

    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))
    whenever(formMapper.requestOf(form)).thenReturn(request)
    whenever(updateUserPasswordUseCase(request)).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onUpdatePassword(form)

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            null,
            null,
            null
          ),
          isLoading = true,
          error = null,
        )
      )

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            null,
            null,
            null
          ),
          isLoading = false,
          error = errorUiModel(),
        )
      )
    }
  }



  @Test
  fun `show new password weak error when new password is invalid`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    mockFormErrorMapper(newPassword = "weak")

    val form = UserProfileUpdatePasswordForm(
      currentPassword = "Old1234!",
      newPassword = "1234",
      newPasswordConfirm = "1234"
    )

    viewModel.uiState.test {

      awaitItem()

      viewModel.onUpdatePassword(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            currentPassword = null,
            newPassword = "weak",
            newPasswordConfirm = null,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show new password weak error when new password equals current password`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    mockFormErrorMapper(newPassword = "weak")

    val form = UserProfileUpdatePasswordForm(
      currentPassword = "Same1234!",
      newPassword = "Same1234!",
      newPasswordConfirm = "Same1234!"
    )

    viewModel.uiState.test {

      awaitItem()

      viewModel.onUpdatePassword(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            currentPassword = null,
            newPassword = "weak",
            newPasswordConfirm = null,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show confirm password blank error when confirm password is blank`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    mockFormErrorMapper(newPasswordConfirm = "error")

    val form = UserProfileUpdatePasswordForm(
      currentPassword = "Old1234!",
      newPassword = "New1234!",
      newPasswordConfirm = ""
    )

    viewModel.uiState.test {

      awaitItem()

      viewModel.onUpdatePassword(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            currentPassword = null,
            newPassword = null,
            newPasswordConfirm = "error",
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show confirm password not match error when confirm password does not match`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    mockFormErrorMapper(newPasswordConfirm = "error")

    val form = UserProfileUpdatePasswordForm(
      currentPassword = "Old1234!",
      newPassword = "New1234!",
      newPasswordConfirm = "Other1234!"
    )

    viewModel.uiState.test {

      awaitItem()

      viewModel.onUpdatePassword(form)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            currentPassword = null,
            newPassword = null,
            newPasswordConfirm = "error",
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `clear current password input error`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    viewModel.uiState.test {

      viewModel.onUpdatePassword(
        UserProfileUpdatePasswordForm(
          currentPassword = "",
          newPassword = "New1234!",
          newPasswordConfirm = "New1234!"
        )
      )
      awaitItem()

      viewModel.onClearInputError(UserProfileUpdatePasswordForm.Input.CurrentPassword)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            currentPassword = null,
            newPassword = null,
            newPasswordConfirm = null,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `clear new password input error`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    viewModel.uiState.test {

      viewModel.onUpdatePassword(
        UserProfileUpdatePasswordForm(
          currentPassword = "Old1234!",
          newPassword = "1234",
          newPasswordConfirm = "1234"
        )
      )
      awaitItem()

      viewModel.onClearInputError(UserProfileUpdatePasswordForm.Input.NewPassword)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            currentPassword = null,
            newPassword = null,
            newPasswordConfirm = null,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `clear confirm password input error`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    viewModel.uiState.test {

      viewModel.onUpdatePassword(
        UserProfileUpdatePasswordForm(
          currentPassword = "Old1234!",
          newPassword = "New1234!",
          newPasswordConfirm = "Other1234!"
        )
      )
      awaitItem()

      viewModel.onClearInputError(UserProfileUpdatePasswordForm.Input.NewPasswordConfirm)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            currentPassword = null,
            newPassword = null,
            newPasswordConfirm = null,
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
    val form = UserProfileUpdatePasswordForm(
      currentPassword = "Old1234!",
      newPassword = "New1234!",
      newPasswordConfirm = "New1234!"
    )
    val request = mock<UpdatePasswordRequest>()
    val error = RuntimeException()

    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))
    whenever(formMapper.requestOf(form)).thenReturn(request)
    whenever(updateUserPasswordUseCase(request)).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onUpdatePassword(form)
      awaitItem()
      awaitItem()

      viewModel.onDismissError()

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileUpdatePasswordUiState.Form(
          user = user,
          form = UserProfileUpdatePasswordForm(),
          formErrors = UserProfileUpdatePasswordFormUiErrors(
            null,
            null,
            null
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  private fun mockFormErrorMapper(
    currentPassword: String? = null,
    newPassword: String? = null,
    newPasswordConfirm: String? = null
  ) {
    whenever { formErrorsMapper.map(any()) } doReturn UserProfileUpdatePasswordFormUiErrors(
      currentPassword = currentPassword,
      newPassword = newPassword,
      newPasswordConfirm = newPasswordConfirm
    )
  }

  private fun basicProfileUser() = User.BasicProfile(
    uid = "uid",
    username = "sergi",
    email = "sergi@test.com",
    photoUrl = null,
    code = "",
    points = 0,
    isSocialAccount = false,
  )
}
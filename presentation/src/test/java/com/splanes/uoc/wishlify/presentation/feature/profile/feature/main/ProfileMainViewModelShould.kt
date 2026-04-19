package com.splanes.uoc.wishlify.presentation.feature.profile.feature.main

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignOutUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchBasicUserProfileUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileMainViewModelShould : UnitTest() {

  private val fetchBasicUserProfileUseCase: FetchBasicUserProfileUseCase = mock()
  private val signOutUseCase: SignOutUseCase = mock()
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: ProfileMainViewModel

  @Before
  fun setup() {
    viewModel = ProfileMainViewModel(
      fetchBasicUserProfileUseCase = fetchBasicUserProfileUseCase,
      signOutUseCase = signOutUseCase,
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `fetch user profile when initializing and show profile when success`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(ProfileMainUiState.Loading)

      val profileState = awaitItem()
      assertThat(profileState).isEqualTo(
        ProfileMainUiState.Profile(
          user = user,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show error when profile fetch returns no user`() = runTest {
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.failure(RuntimeException()))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(ProfileMainUiState.Loading)

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(ProfileMainUiState.Error)
    }
  }

  @Test
  fun `fetch profile again when profile is updated`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(
      Result.success(user),
      Result.success(user),
    )

    viewModel.uiState.test {

      viewModel.onProfileUpdated()

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(ProfileMainUiState.Loading)

      val profileState = awaitItem()
      assertThat(profileState).isEqualTo(
        ProfileMainUiState.Profile(
          user = user,
          isLoading = false,
          error = null,
        )
      )

      verify(fetchBasicUserProfileUseCase, times(2)).invoke()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `sign out and update loading state`() = runTest {
    val user = basicProfileUser()
    whenever(fetchBasicUserProfileUseCase()).thenReturn(Result.success(user))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onSignOut()

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        ProfileMainUiState.Profile(
          user = user,
          isLoading = true,
          error = null,
        )
      )

      advanceUntilIdle()

      val finalState = awaitItem()
      assertThat(finalState).isEqualTo(
        ProfileMainUiState.Profile(
          user = user,
          isLoading = false,
          error = null,
        )
      )

      verify(signOutUseCase).invoke()
    }
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
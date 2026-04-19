package com.splanes.uoc.wishlify.presentation.feature.profile.feature.hobbies

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.user.model.Hobbies
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserHobbiesUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.UpdateUserProfileUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileHobbiesViewModelShould : UnitTest() {

  private val fetchUserHobbiesUseCase: FetchUserHobbiesUseCase = mock()
  private val updateUserProfileUseCase: UpdateUserProfileUseCase = mock()
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: ProfileHobbiesViewModel

  @Before
  fun setup() {
    viewModel = ProfileHobbiesViewModel(
      fetchUserHobbiesUseCase = fetchUserHobbiesUseCase,
      updateUserProfileUseCase = updateUserProfileUseCase,
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `fetch user hobbies when initializing and show hobbies when success`() = runTest {
    val user = hobbiesProfileUser()
    whenever(fetchUserHobbiesUseCase()).thenReturn(Result.success(user))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(ProfileHobbiesUiState.Loading)

      val hobbiesState = awaitItem()
      assertThat(hobbiesState).isEqualTo(
        ProfileHobbiesUiState.Hobbies(
          user = user,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show error when hobbies fetch fails`() = runTest {
    whenever(fetchUserHobbiesUseCase()).thenReturn(Result.failure(RuntimeException()))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(ProfileHobbiesUiState.Loading)

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(ProfileHobbiesUiState.Error)
    }
  }

  @Test
  fun `update hobbies and emit side effect when success`() = runTest {
    val user = hobbiesProfileUser()
    whenever(fetchUserHobbiesUseCase()).thenReturn(Result.success(user))
    whenever(updateUserProfileUseCase(any())).thenReturn(Result.success(Unit))

    val hobbies = listOf("Anime", "Gaming")

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      uiStateTurbine.awaitItem()
      uiStateTurbine.awaitItem()

      viewModel.onUpdateHobbies(
        enabled = true,
        hobbies = hobbies
      )

      val loadingState = uiStateTurbine.awaitItem()
      assertThat(loadingState).isEqualTo(
        ProfileHobbiesUiState.Hobbies(
          user = user,
          isLoading = true,
          error = null,
        )
      )

      val finalState = uiStateTurbine.awaitItem()
      assertThat(finalState).isEqualTo(
        ProfileHobbiesUiState.Hobbies(
          user = user,
          isLoading = false,
          error = null,
        )
      )

      val effect = sideEffectTurbine.awaitItem()
      assertThat(effect).isEqualTo(ProfileHobbiesUiSideEffect.HobbiesUpdated)

      verify(updateUserProfileUseCase).invoke(
        UpdateProfileRequest.Hobbies(
          user = user,
          enabled = true,
          values = hobbies,
        )
      )

      uiStateTurbine.cancelAndIgnoreRemainingEvents()
      sideEffectTurbine.cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `show error when update hobbies fails`() = runTest {
    val user = hobbiesProfileUser()
    val error = RuntimeException()

    whenever(fetchUserHobbiesUseCase()).thenReturn(Result.success(user))
    whenever(updateUserProfileUseCase(any())).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    val hobbies = listOf("Anime", "Gaming")

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onUpdateHobbies(
        enabled = true,
        hobbies = hobbies
      )

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        ProfileHobbiesUiState.Hobbies(
          user = user,
          isLoading = true,
          error = null,
        )
      )

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        ProfileHobbiesUiState.Hobbies(
          user = user,
          isLoading = false,
          error = errorUiModel(),
        )
      )
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val user = hobbiesProfileUser()
    val error = RuntimeException()

    whenever(fetchUserHobbiesUseCase()).thenReturn(Result.success(user))
    whenever(updateUserProfileUseCase(any())).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onUpdateHobbies(
        enabled = true,
        hobbies = listOf("Anime")
      )
      awaitItem()
      awaitItem()

      viewModel.onDismissError()

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileHobbiesUiState.Hobbies(
          user = user,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  private fun hobbiesProfileUser() = User.HobbiesProfile(
    uid = "uid",
    username = "sergi",
    code = "",
    photoUrl = null,
    hobbies = Hobbies(
      true,
      values = listOf("Anime", "Gaming")
    )
  )
}
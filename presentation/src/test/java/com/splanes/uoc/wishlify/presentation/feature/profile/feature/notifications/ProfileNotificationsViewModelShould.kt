package com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.user.model.NotificationPermissions
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserNotificationsUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.UpdateUserProfileUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.model.UserProfileNotificationsForm
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileNotificationsViewModelShould : UnitTest() {

  private val fetchUserNotificationsUseCase: FetchUserNotificationsUseCase = mock()
  private val updateUserProfileUseCase: UpdateUserProfileUseCase = mock()
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: ProfileNotificationsViewModel

  @Before
  fun setup() {
    viewModel = ProfileNotificationsViewModel(
      fetchUserNotificationsUseCase = fetchUserNotificationsUseCase,
      updateUserProfileUseCase = updateUserProfileUseCase,
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `fetch notifications profile when initializing and show notifications when success`() = runTest {
    val user = notificationsProfileUser()
    whenever(fetchUserNotificationsUseCase()).thenReturn(Result.success(user))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(ProfileNotificationsUiState.Loading)

      val notificationsState = awaitItem()
      assertThat(notificationsState).isEqualTo(
        ProfileNotificationsUiState.Notifications(
          user = user,
          form = UserProfileNotificationsForm(
            sharedWishlistChat = true,
            sharedWishlistUpdates = false,
            sharedWishlistsDeadlineReminders = true,
            secretSantaChat = false,
            secretSantaDeadlineReminders = true,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show error when notifications profile fetch fails`() = runTest {
    whenever(fetchUserNotificationsUseCase()).thenReturn(Result.failure(RuntimeException()))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(ProfileNotificationsUiState.Loading)

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(ProfileNotificationsUiState.Error)
    }
  }

  @Test
  fun `update notifications and emit side effect when success`() = runTest {
    val user = notificationsProfileUser()
    whenever(fetchUserNotificationsUseCase()).thenReturn(Result.success(user))
    whenever(updateUserProfileUseCase(any())).thenReturn(Result.success(Unit))

    val form = UserProfileNotificationsForm(
      sharedWishlistChat = false,
      sharedWishlistUpdates = true,
      sharedWishlistsDeadlineReminders = false,
      secretSantaChat = true,
      secretSantaDeadlineReminders = false,
    )

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      uiStateTurbine.awaitItem()
      uiStateTurbine.awaitItem()

      viewModel.onUpdateNotifications(form)

      val loadingState = uiStateTurbine.awaitItem()
      assertThat(loadingState).isEqualTo(
        ProfileNotificationsUiState.Notifications(
          user = user,
          form = UserProfileNotificationsForm(
            sharedWishlistChat = true,
            sharedWishlistUpdates = false,
            sharedWishlistsDeadlineReminders = true,
            secretSantaChat = false,
            secretSantaDeadlineReminders = true,
          ),
          isLoading = true,
          error = null,
        )
      )

      val finalState = uiStateTurbine.awaitItem()
      assertThat(finalState).isEqualTo(
        ProfileNotificationsUiState.Notifications(
          user = user,
          form = UserProfileNotificationsForm(
            sharedWishlistChat = true,
            sharedWishlistUpdates = false,
            sharedWishlistsDeadlineReminders = true,
            secretSantaChat = false,
            secretSantaDeadlineReminders = true,
          ),
          isLoading = false,
          error = null,
        )
      )

      val effect = sideEffectTurbine.awaitItem()
      assertThat(effect).isEqualTo(ProfileNotificationsUiSideEffect.NotificationsUpdated)

      verify(updateUserProfileUseCase).invoke(
        UpdateProfileRequest.Notifications(
          user = user,
          sharedWishlistChat = false,
          sharedWishlistUpdates = true,
          sharedWishlistsDeadlineReminders = false,
          secretSantaChat = true,
          secretSantaDeadlineReminders = false,
        )
      )

      uiStateTurbine.cancelAndIgnoreRemainingEvents()
      sideEffectTurbine.cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `show error when update notifications fails`() = runTest {
    val user = notificationsProfileUser()
    val error = RuntimeException()

    whenever(fetchUserNotificationsUseCase()).thenReturn(Result.success(user))
    whenever(updateUserProfileUseCase(any())).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    val form = UserProfileNotificationsForm(
      sharedWishlistChat = false,
      sharedWishlistUpdates = true,
      sharedWishlistsDeadlineReminders = false,
      secretSantaChat = true,
      secretSantaDeadlineReminders = false,
    )

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onUpdateNotifications(form)

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        ProfileNotificationsUiState.Notifications(
          user = user,
          form = UserProfileNotificationsForm(
            sharedWishlistChat = true,
            sharedWishlistUpdates = false,
            sharedWishlistsDeadlineReminders = true,
            secretSantaChat = false,
            secretSantaDeadlineReminders = true,
          ),
          isLoading = true,
          error = null,
        )
      )

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        ProfileNotificationsUiState.Notifications(
          user = user,
          form = UserProfileNotificationsForm(
            sharedWishlistChat = true,
            sharedWishlistUpdates = false,
            sharedWishlistsDeadlineReminders = true,
            secretSantaChat = false,
            secretSantaDeadlineReminders = true,
          ),
          isLoading = false,
          error = errorUiModel(),
        )
      )
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val user = notificationsProfileUser()
    val error = RuntimeException()

    whenever(fetchUserNotificationsUseCase()).thenReturn(Result.success(user))
    whenever(updateUserProfileUseCase(any())).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onUpdateNotifications(UserProfileNotificationsForm())
      awaitItem()
      awaitItem()

      viewModel.onDismissError()

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        ProfileNotificationsUiState.Notifications(
          user = user,
          form = UserProfileNotificationsForm(
            sharedWishlistChat = true,
            sharedWishlistUpdates = false,
            sharedWishlistsDeadlineReminders = true,
            secretSantaChat = false,
            secretSantaDeadlineReminders = true,
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  private fun notificationsProfileUser() = User.NotificationsProfile(
    uid = "uid",
    username = "sergi",
    code = "",
    photoUrl = null,
    notificationPermissions = NotificationPermissions(
      sharedWishlistChat = true,
      sharedWishlistUpdates = false,
      sharedWishlistsDeadlineReminders = true,
      secretSantaChat = false,
      secretSantaDeadlineReminders = true,
    ),
  )
}
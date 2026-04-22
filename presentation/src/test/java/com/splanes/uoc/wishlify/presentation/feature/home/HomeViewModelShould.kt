package com.splanes.uoc.wishlify.presentation.feature.home

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.session.model.SessionState
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetSessionStateFlowUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelShould : UnitTest() {

  private val getSessionStateFlowUseCase: GetSessionStateFlowUseCase = mock()

  private lateinit var viewModel: HomeViewModel

  @Before
  fun setup() {
    viewModel = HomeViewModel(
      getSessionStateFlowUseCase = getSessionStateFlowUseCase,
      deeplinkMapper = mock()
    )
  }

  @Test
  fun `emit no session when session state becomes signed out`() = runTest {
    val sessionStateFlow = MutableSharedFlow<SessionState>()
    whenever(getSessionStateFlowUseCase()).thenReturn(sessionStateFlow)

    viewModel.uiSideEffect.test {
      viewModel.observeSessionState()
      advanceUntilIdle()

      sessionStateFlow.emit(SessionState.SignedOut)

      val effect = awaitItem()
      assertThat(effect).isEqualTo(HomeUiSideEffect.NoSession)
    }
  }

  @Test
  fun `do nothing when session state is not signed out`() = runTest {
    val sessionStateFlow = MutableSharedFlow<SessionState>()
    whenever(getSessionStateFlowUseCase()).thenReturn(sessionStateFlow)

    viewModel.uiSideEffect.test {
      viewModel.observeSessionState()
      advanceUntilIdle()

      sessionStateFlow.emit(SessionState.SignedIn)

      expectNoEvents()
    }
  }

  @Test
  fun `not start a second observer when session state is already being observed`() = runTest {
    val sessionStateFlow = MutableSharedFlow<SessionState>()
    whenever(getSessionStateFlowUseCase()).thenReturn(sessionStateFlow)

    viewModel.observeSessionState()
    advanceUntilIdle()
    viewModel.observeSessionState()
    advanceUntilIdle()

    verify(getSessionStateFlowUseCase, times(1)).invoke()
  }

  @Test
  fun `stop observing session state when observer is cancelled`() = runTest {
    val sessionStateFlow = MutableSharedFlow<SessionState>()
    whenever(getSessionStateFlowUseCase()).thenReturn(sessionStateFlow)

    viewModel.uiSideEffect.test {
      viewModel.observeSessionState()
      advanceUntilIdle()
      viewModel.cancelSessionStateObserver()

      sessionStateFlow.emit(SessionState.SignedOut)

      expectNoEvents()
    }
  }

  @Test
  fun `start observing session state again after observer is cancelled`() = runTest {
    val sessionStateFlow = MutableSharedFlow<SessionState>()
    whenever(getSessionStateFlowUseCase()).thenReturn(sessionStateFlow)

    viewModel.uiSideEffect.test {
      viewModel.observeSessionState()
      advanceUntilIdle()
      viewModel.cancelSessionStateObserver()
      viewModel.observeSessionState()
      advanceUntilIdle()

      sessionStateFlow.emit(SessionState.SignedOut)

      val effect = awaitItem()
      assertThat(effect).isEqualTo(HomeUiSideEffect.NoSession)
    }

    verify(getSessionStateFlowUseCase, times(2)).invoke()
  }
}
package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaEventsUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class SecretSantaListViewModelShould : UnitTest() {

  private val fetchSecretSantaEventsUseCase: FetchSecretSantaEventsUseCase = mock()
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: SecretSantaListViewModel

  @Before
  fun setup() {
    viewModel = SecretSantaListViewModel(
      fetchSecretSantaEventsUseCase = fetchSecretSantaEventsUseCase,
      addEventParticipantFromLinkUseCase = mock(),
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `fetch secret santa events on init and show loading then empty when there are no events`() = runTest {
    whenever(fetchSecretSantaEventsUseCase())
      .thenReturn(Result.success(emptyList()))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(SecretSantaListUiState.Loading)

      val emptyState = awaitItem()
      assertThat(emptyState).isEqualTo(
        SecretSantaListUiState.Empty(
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `fetch secret santa events on init and show events when there are results`() = runTest {
    val events = listOf(secretSantaEvent())

    whenever(fetchSecretSantaEventsUseCase())
      .thenReturn(Result.success(events))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(SecretSantaListUiState.Loading)

      val eventsState = awaitItem()
      assertThat(eventsState).isInstanceOf(SecretSantaListUiState.Events::class.java)
    }
  }

  @Test
  fun `show error when fetch secret santa events fails`() = runTest {
    val error = RuntimeException()

    whenever(fetchSecretSantaEventsUseCase())
      .thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        SecretSantaListUiState.Empty(
          isLoading = false,
          error = errorUiModel(),
        )
      )
    }
  }

  @Test
  fun `reload events when new event result is received`() = runTest {
    whenever(fetchSecretSantaEventsUseCase())
      .thenReturn(
        Result.success(emptyList()),
        Result.success(listOf(secretSantaEvent()))
      )

    viewModel.uiState.test {

      viewModel.onNewEventResult()

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(SecretSantaListUiState.Loading)

      val eventsState = awaitItem()
      assertThat(eventsState).isInstanceOf(SecretSantaListUiState.Events::class.java)

      verify(fetchSecretSantaEventsUseCase, times(2)).invoke()
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val error = RuntimeException()

    whenever(fetchSecretSantaEventsUseCase())
      .thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onDismissError()

      val state = awaitItem()
      assertThat(state).isEqualTo(
        SecretSantaListUiState.Empty(
          isLoading = false,
          error = null,
        )
      )
    }
  }

  private fun secretSantaEvent(
    id: String = "",
    name: String = "",
    photoUrl: String? = null,
    deadline: Date = Date(1L),
    target: String = ""
  ): SecretSantaEvent =
    SecretSantaEvent.DrawDone(
      id = id,
      name = name,
      photoUrl = photoUrl,
      deadline = deadline,
      target = target
    )
}
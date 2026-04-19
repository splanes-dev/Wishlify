package com.splanes.uoc.wishlify.presentation.feature.groups.feature.search

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.SearchUserUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GroupsSearchUsersViewModelShould : UnitTest() {

  private val searchUserUseCase: SearchUserUseCase = mock()
  private val errorUiMapper: ErrorUiMapper = mock()
  private lateinit var viewModel: GroupsSearchUsersViewModel

  @Before
  fun setup() {
    viewModel = GroupsSearchUsersViewModel(
      searchUserUseCase = searchUserUseCase,
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `have initial state`() = runTest {
    viewModel.uiState.test {
      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        GroupsSearchUsersUiState(
          searchQuery = "",
          results = emptyList(),
          added = emptyList(),
          isInfoBannerVisible = true,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `search users and update results when use case succeeds`() = runTest {
    val query = "sergi"
    val users = listOf(
      basicUser(uid = "1", username = "sergi"),
      basicUser(uid = "2", username = "sergi2"),
    )
    whenever(searchUserUseCase(query)).thenReturn(Result.success(users))

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSearch(query)

      val successState = awaitItem()
      assertThat(successState).isEqualTo(
        GroupsSearchUsersUiState(
          searchQuery = query,
          results = users,
          added = emptyList(),
          isInfoBannerVisible = true,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show error when search use case fails`() = runTest {
    val query = "sergi"
    val error = RuntimeException()
    whenever(searchUserUseCase(query)).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSearch(query)

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        GroupsSearchUsersUiState(
          searchQuery = query,
          results = emptyList(),
          added = emptyList(),
          isInfoBannerVisible = true,
          isLoading = false,
          error = errorUiModel(),
        )
      )
    }
  }

  @Test
  fun `add user to added list`() = runTest {
    val user = basicUser(uid = "1", username = "sergi")

    viewModel.uiState.test {
      awaitItem()

      viewModel.onAddUser(user)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        GroupsSearchUsersUiState(
          searchQuery = "",
          results = emptyList(),
          added = listOf(user),
          isInfoBannerVisible = true,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `not duplicate user when adding same user twice`() = runTest {
    val user = basicUser(uid = "1", username = "sergi")

    viewModel.uiState.test {
      viewModel.onAddUser(user)
      awaitItem()

      viewModel.onAddUser(user)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        GroupsSearchUsersUiState(
          searchQuery = "",
          results = emptyList(),
          added = listOf(user),
          isInfoBannerVisible = true,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `remove user from added list`() = runTest {
    val user1 = basicUser(uid = "1", username = "sergi")
    val user2 = basicUser(uid = "2", username = "anna")

    viewModel.uiState.test {
      awaitItem()

      viewModel.onAddUser(user1)
      awaitItem()

      viewModel.onAddUser(user2)
      awaitItem()

      viewModel.onRemoveUser(user1)

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        GroupsSearchUsersUiState(
          searchQuery = "",
          results = emptyList(),
          added = listOf(user2),
          isInfoBannerVisible = true,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `close info banner`() = runTest {
    viewModel.uiState.test {
      awaitItem()

      viewModel.onCloseInfoBanner()

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        GroupsSearchUsersUiState(
          searchQuery = "",
          results = emptyList(),
          added = emptyList(),
          isInfoBannerVisible = false,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val query = "sergi"
    val error = RuntimeException()
    whenever(searchUserUseCase(query)).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()

      viewModel.onSearch(query)
      awaitItem()

      viewModel.onDismissError()

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        GroupsSearchUsersUiState(
          searchQuery = query,
          results = emptyList(),
          added = emptyList(),
          isInfoBannerVisible = true,
          isLoading = false,
          error = null,
        )
      )
    }
  }

  private fun basicUser(
    uid: String,
    username: String,
    photoUrl: String? = null,
  ) = User.Basic(
    uid = uid,
    username = username,
    photoUrl = photoUrl,
    code = ""
  )
}
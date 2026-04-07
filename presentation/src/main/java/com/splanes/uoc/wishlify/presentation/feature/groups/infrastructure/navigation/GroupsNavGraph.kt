package com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.GroupsListRoute
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.GroupsListViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.GroupsNewGroupRoute
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.GroupsNewGroupViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.search.GroupsSearchUsersRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.NavResultHandler
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.Transitions
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.popBackStackWithResult
import org.koin.androidx.compose.koinViewModel

class GroupsNavGraph : FeatureHomeNavGraph {

  override val position: Int = 3

  override fun isNavigationBarVisible(selected: String): Boolean =
    selected == Groups.List::class.qualifiedName

  @Composable
  override fun RowScope.NavigationBarItem(selected: String, navController: NavHostController) {
    NavigationBarItem(
      selected = selected == Groups.List::class.qualifiedName,
      onClick = {
        navController.navigate(Groups) {
          launchSingleTop = true
          popUpTo(navController.graph.id) {
            inclusive = true
          }
        }
      },
      icon = {
        Icon(
          imageVector = Icons.Outlined.Groups,
          contentDescription = stringResource(R.string.tab_groups),
        )
      },
      label = { Text(text = stringResource(R.string.tab_groups)) },
      alwaysShowLabel = false,
    )
  }

  override fun NavGraphBuilder.buildNavGraph(
    navController: NavHostController,
    onLogout: (NavOptionsBuilder.() -> Unit) -> Unit,
  ) {
    navigation<Groups>(startDestination = Groups.List) {
      composable<Groups.List> {
        val viewModel = koinViewModel<GroupsListViewModel>()

        navController.NavResultHandler<Boolean>(key = NavResult.NEW_GROUP) { created ->
          viewModel.onCreateGroupResult(created)
        }

        GroupsListRoute(
          viewModel = viewModel,
          onNavToNewGroup = {
            navController.navigate(Groups.NewGroup)
          }
        )
      }

      composable<Groups.NewGroup>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) {
        val viewModel = koinViewModel<GroupsNewGroupViewModel>()

        navController.NavResultHandler<List<String>>(key = NavResult.SEARCH) { users ->
          if (users.isNotEmpty()) {
            viewModel.onUserSearchResult(users)
          }
        }

        GroupsNewGroupRoute(
          viewModel = viewModel,
          onNavToSearchUsers = {
            navController.navigate(Groups.SearchUsers)
          },
          onFinish = { result ->
            navController.popBackStackWithResult(
              key = NavResult.NEW_GROUP,
              result = result
            )
          },
        )
      }

      composable<Groups.SearchUsers>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit,
      ) {
        GroupsSearchUsersRoute(
          viewModel = koinViewModel(),
          onFinish = { result ->
            navController.popBackStackWithResult(
              key = NavResult.SEARCH,
              result = result
            )
          },
        )
      }
    }
  }
}

private object NavResult {
  const val NEW_GROUP = "new-group"
  const val SEARCH = "search-users"
}
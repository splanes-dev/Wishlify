package com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail.GroupDetailRoute
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail.GroupDetailViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.GroupsListRoute
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.GroupsListViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.GroupsNewGroupRoute
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.GroupsNewGroupViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.edition.GroupsEditGroupRoute
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.edition.GroupsEditGroupViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.search.GroupsSearchUsersRoute
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation.SecretSanta
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlists
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.NavResultHandler
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.Transitions
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.popBackStackWithResult
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/** Home navigation graph that hosts the groups feature and its subflows. */
class GroupsNavGraph : FeatureHomeNavGraph {

  /** Position of the groups tab inside the home navigation bar. */
  override val position: Int = 3

  /** Keeps the bottom bar visible only on the groups list root destination. */
  override fun isNavigationBarVisible(destination: NavDestination?): Boolean =
    destination?.hasRoute(Groups.List::class) == true

  /** Renders the navigation bar item that enters the groups root graph. */
  @Composable
  override fun RowScope.NavigationBarItem(
    current: NavDestination?,
    navController: NavHostController
  ) {
    NavigationBarItem(
      selected = current?.hasRoute(Groups.List::class) == true,
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

  /** Registers the groups destinations and the result-based navigation wiring between them. */
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

        navController.NavResultHandler<Boolean>(key = NavResult.UPDATE_GROUP) { updated ->
          if (updated) {
            viewModel.onGroupUpdated()
          }
        }

        GroupsListRoute(
          viewModel = viewModel,
          onNavToDetail = { groupId, name ->
            val route = Groups.Detail(groupId, name)
            navController.navigate(route)
          },
          onNavToEdit = { groupId, name ->
            val route = Groups.EditGroup(groupId, name)
            navController.navigate(route)
          },
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

      composable<Groups.EditGroup>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<Groups.EditGroup>()
        val viewModel = koinViewModel<GroupsEditGroupViewModel> {
          parametersOf(
            route.groupId,
            route.groupName
          )
        }

        navController.NavResultHandler<List<String>>(key = NavResult.SEARCH) { users ->
          if (users.isNotEmpty()) {
            viewModel.onUserSearchResult(users)
          }
        }

        GroupsEditGroupRoute(
          viewModel = viewModel,
          onNavToSearchUsers = {
            navController.navigate(Groups.SearchUsers)
          },
          onFinish = { result ->
            navController.popBackStackWithResult(
              key = NavResult.UPDATE_GROUP,
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

      composable<Groups.Detail>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit,
      ) { backStackEntry ->

        val route = backStackEntry.toRoute<Groups.Detail>()

        val viewModel = koinViewModel<GroupDetailViewModel> {
          parametersOf(route.groupId, route.groupName)
        }

        navController.NavResultHandler<Boolean>(key = NavResult.UPDATE_GROUP) { updated ->
          if (updated) {
            viewModel.onGroupUpdated()
          }
        }

        GroupDetailRoute(
          viewModel = viewModel,
          onNavToEdit = { groupId, name ->
            val route = Groups.EditGroup(groupId, name)
            navController.navigate(route)
          },
          onNavToSharedWishlist = { wishlist ->
            val route = SharedWishlists.ThirdPartyDetail(
              wishlist.id,
              wishlist.linkedWishlist.name,
              wishlist.linkedWishlist.target.orEmpty()
            )
            navController.navigate(route)
          },
          onNavToSecretSanta = { event ->
            val route = SecretSanta.Detail(event.id, event.name)
            navController.navigate(route)
          },
          onFinish = { result ->
            navController.popBackStackWithResult(
              key = NavResult.UPDATE_GROUP,
              result = result,
            )
          }
        )
      }
    }
  }
}

private object NavResult {
  const val NEW_GROUP = "new-group"
  const val UPDATE_GROUP = "update-group"
  const val SEARCH = "search-users"
}

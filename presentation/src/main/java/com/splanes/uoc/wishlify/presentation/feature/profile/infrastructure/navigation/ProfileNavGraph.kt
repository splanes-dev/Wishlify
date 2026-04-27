package com.splanes.uoc.wishlify.presentation.feature.profile.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
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
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.hobbies.ProfileHobbiesRoute
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.main.ProfileMainRoute
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.main.ProfileMainViewModel
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.ProfileNotificationsRoute
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.ProfileUpdatePasswordRoute
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.ProfileUpdateRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.NavResultHandler
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.Transitions
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.popBackStackWithResult
import org.koin.androidx.compose.koinViewModel

/**
 * Home navigation graph that hosts the profile flows.
 */
class ProfileNavGraph : FeatureHomeNavGraph {

  /**
   * Position of the profile tab in the home navigation bar.
   */
  override val position: Int = 4

  /**
   * Keeps the bottom navigation visible only on the main profile screen.
   */
  override fun isNavigationBarVisible(destination: NavDestination?): Boolean =
    destination?.hasRoute(Profile.Main::class) == true

  /**
   * Renders the profile item in the home navigation bar.
   */
  @Composable
  override fun RowScope.NavigationBarItem(
    current: NavDestination?,
    navController: NavHostController
  ) {
    NavigationBarItem(
      selected = current?.hasRoute(Profile.Main::class) == true,
      onClick = {
        navController.navigate(Profile) {
          launchSingleTop = true
          popUpTo(navController.graph.id) {
            inclusive = true
          }
        }
      },
      icon = {
        Icon(
          imageVector = Icons.Outlined.Person,
          contentDescription = stringResource(R.string.tab_profile),
        )
      },
      label = { Text(text = stringResource(R.string.tab_profile)) },
      alwaysShowLabel = false,
    )
  }

  /**
   * Registers the profile feature graph, including update, password, hobbies and notifications
   * flows.
   */
  override fun NavGraphBuilder.buildNavGraph(
    navController: NavHostController,
    onLogout: (NavOptionsBuilder.() -> Unit) -> Unit,
  ) {
    navigation<Profile>(startDestination = Profile.Main) {
      composable<Profile.Main> {

        val viewModel = koinViewModel<ProfileMainViewModel>()

        navController.NavResultHandler<Boolean>(key = NavResult.PROFILE_UPDATED) { updated ->
          if (updated) {
            viewModel.onProfileUpdated()
          }
        }

        ProfileMainRoute(
          viewModel = viewModel,
          onNavToUpdateProfile = {
            navController.navigate(Profile.UpdateProfile)
          },
          onNavToChangePassword = {
            navController.navigate(Profile.UpdatePassword)
          },
          onNavToAdminNotifications = {
            navController.navigate(Profile.Notifications)
          },
          onNavToStore = {

          },
          onNavToHobbies = {
            navController.navigate(Profile.Hobbies)
          }
        )
      }

      composable<Profile.UpdateProfile>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit,
      ) {
        ProfileUpdateRoute(
          viewModel = koinViewModel(),
          onFinish = {
            navController.popBackStackWithResult(
              key = NavResult.PROFILE_UPDATED,
              result = it
            )
          },
        )
      }

      composable<Profile.UpdatePassword>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit,
      ) {
        ProfileUpdatePasswordRoute(
          viewModel = koinViewModel(),
          onBack = { navController.popBackStack() }
        )
      }

      composable<Profile.Hobbies>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit,
      ) {
        ProfileHobbiesRoute(
          viewModel = koinViewModel(),
          onBack = { navController.popBackStack() }
        )
      }

      composable<Profile.Notifications>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit,
      ) {
        ProfileNotificationsRoute(
          viewModel = koinViewModel(),
          onBack = { navController.popBackStack() }
        )
      }
    }
  }
}

/**
 * Navigation result keys shared between profile destinations.
 */
private object NavResult {
  const val PROFILE_UPDATED = "updated-profile"
}

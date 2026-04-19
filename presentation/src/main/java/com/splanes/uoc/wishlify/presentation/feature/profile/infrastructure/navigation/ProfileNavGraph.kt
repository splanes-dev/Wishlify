package com.splanes.uoc.wishlify.presentation.feature.profile.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
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

class ProfileNavGraph : FeatureHomeNavGraph {

  override val position: Int = 4

  override fun isNavigationBarVisible(selected: String): Boolean =
    selected == Profile.Main::class.qualifiedName

  @Composable
  override fun RowScope.NavigationBarItem(selected: String, navController: NavHostController) {
    NavigationBarItem(
      selected = selected == Profile.Main::class.qualifiedName,
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

private object NavResult {
  const val PROFILE_UPDATED = "updated-profile"
}
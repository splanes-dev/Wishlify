package com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.navigation

import androidx.navigation.NavHostController
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.HomeLauncher

/** Navigation launcher that redirects the user into the home graph. */
class HomeLauncherImpl(private val navController: NavHostController) : HomeLauncher {
  /** Navigates to the home graph and clears the provided back-stack origin. */
  override fun launch(popUpTo: Any) {
    navController.navigate(Home) {
      launchSingleTop = true
      popUpTo(popUpTo) {
        inclusive = true
      }
    }
  }
}

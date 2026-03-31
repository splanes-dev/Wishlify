package com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.navigation

import androidx.navigation.NavHostController
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.HomeLauncher

class HomeLauncherImpl(private val navController: NavHostController) : HomeLauncher {
  override fun launch(popUpTo: Any) {
    navController.navigate(Home) {
      launchSingleTop = true
      popUpTo(popUpTo) {
        inclusive = true
      }
    }
  }
}
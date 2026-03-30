package com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.navigation

import androidx.navigation.NavHostController
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.AuthLauncher

class AuthLauncherImpl(private val navController: NavHostController) : AuthLauncher {
  override fun launch(popUpTo: Any) {
    navController.navigate(route = Auth) {
      popUpTo(route = popUpTo) {
        inclusive = true
      }
    }
  }
}
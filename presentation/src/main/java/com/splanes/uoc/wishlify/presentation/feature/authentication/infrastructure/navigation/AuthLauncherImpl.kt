package com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.navigation

import androidx.navigation.NavHostController
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.AuthLauncher

/** Navigation launcher that redirects the user into the authentication graph. */
class AuthLauncherImpl(private val navController: NavHostController) : AuthLauncher {
  /** Navigates to the auth graph and clears the provided back-stack origin. */
  override fun launch(popUpTo: Any) {
    navController.navigate(route = Auth) {
      popUpTo(route = popUpTo) {
        inclusive = true
      }
    }
  }
}

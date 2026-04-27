package com.splanes.uoc.wishlify.presentation.feature.splash.infrastructure.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.splanes.uoc.wishlify.presentation.feature.splash.SplashRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureMainNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.AuthLauncher
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

/**
 * Main navigation graph entry that hosts the splash destination and forwards the user into the
 * authentication flow.
 */
class SplashNavGraph : FeatureMainNavGraph {

  /**
   * Registers the splash destination as the main entry point of the app shell.
   */
  override fun NavGraphBuilder.buildNavGraph(navController: NavHostController) {
    composable<Splash> {
      val authLauncher = koinInject<AuthLauncher> { parametersOf(navController) }

      SplashRoute(
        onNavToAuth = { authLauncher.launch(popUpTo = Splash) }
      )
    }
  }
}

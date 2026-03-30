package com.splanes.uoc.wishlify.presentation.feature.splash.infrastructure.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.splanes.uoc.wishlify.presentation.feature.splash.SplashRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureMainNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.AuthLauncher
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

class SplashNavGraph : FeatureMainNavGraph {

  override fun NavGraphBuilder.buildNavGraph(navController: NavHostController) {
    composable<Splash> {
      val authLauncher = koinInject<AuthLauncher> { parametersOf(navController) }

      SplashRoute(
        onNavToAuth = { authLauncher.launch(popUpTo = Splash) }
      )
    }
  }
}
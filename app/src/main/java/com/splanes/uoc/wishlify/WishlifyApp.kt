package com.splanes.uoc.wishlify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureMainNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

/**
 * Root Compose entry point of the application.
 *
 * It hosts the main navigation graph set and applies the shared window
 * insets and background expected by the app shell.
 */
@Composable
fun WishlifyApp(
  navController: NavHostController,
  navGraphs: List<FeatureMainNavGraph>,
  startDestination: Any
) {

  NavHost(
    modifier = Modifier
      .fillMaxSize()
      .background(WishlifyTheme.colorScheme.background)
      .imePadding()
      .systemBarsPadding(),
    navController = navController,
    startDestination = startDestination,
  ) {
    navGraphs.forEach { navGraph ->
      navGraph.run { buildNavGraph(navController) }
    }
  }
}

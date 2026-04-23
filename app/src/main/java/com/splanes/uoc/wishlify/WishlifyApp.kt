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
package com.splanes.uoc.wishlify.presentation.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.HomeNavStartRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.AuthLauncher
import org.koin.compose.currentKoinScope
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

@Composable
fun HomeRoute(mainNavController: NavHostController) {
  val startDestination = koinInject<Any>(named(HomeNavStartRoute))
  val authLauncher = koinInject<AuthLauncher> { parametersOf(mainNavController) }
  val navController = rememberNavController()
  val navGraphs = currentKoinScope().getAll<FeatureHomeNavGraph>()
  val current by navController.currentBackStackEntryAsState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
  ) {
    NavHost(
      modifier = Modifier.fillMaxSize(),
      navController = navController,
      startDestination = startDestination
    ) {
      navGraphs.forEach { navGraph ->
        navGraph.run { buildNavGraph(navController, authLauncher::launch) }
      }
    }

    if (current.isNavigationBarVisible(navGraphs)) {
      NavigationBar(
        modifier = Modifier
          .height(72.dp)
          .fillMaxWidth()
          .align(Alignment.BottomCenter),
      ) {
        navGraphs
          .sortedBy { navGraph -> navGraph.position }
          .forEach { navGraph ->
            navGraph.run { NavigationBarItem(current.routeOrEmpty, navController) }
          }
      }
    }
  }
}

private val NavBackStackEntry?.routeOrEmpty
  get() = this?.destination?.route.orEmpty()

private fun NavBackStackEntry?.isNavigationBarVisible(graphs: List<FeatureHomeNavGraph>): Boolean =
  graphs.any { graph -> graph.isNavigationBarVisible(routeOrEmpty) }
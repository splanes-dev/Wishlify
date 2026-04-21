package com.splanes.uoc.wishlify.presentation.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

sealed interface FeatureNavGraph

interface FeatureMainNavGraph : FeatureNavGraph {
  fun NavGraphBuilder.buildNavGraph(navController: NavHostController)
}

interface FeatureHomeNavGraph : FeatureNavGraph {

  val position: Int

  fun isNavigationBarVisible(destination: NavDestination?): Boolean

  fun NavGraphBuilder.buildNavGraph(
    navController: NavHostController,
    onLogout: (NavOptionsBuilder.() -> Unit) -> Unit
  )

  @Composable
  fun RowScope.NavigationBarItem(current: NavDestination?, navController: NavHostController)
}
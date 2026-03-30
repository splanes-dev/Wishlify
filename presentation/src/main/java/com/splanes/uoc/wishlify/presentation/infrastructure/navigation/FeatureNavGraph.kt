package com.splanes.uoc.wishlify.presentation.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

sealed interface FeatureNavGraph

interface FeatureMainNavGraph : FeatureNavGraph {
  fun NavGraphBuilder.buildNavGraph(navController: NavHostController)
}

interface FeatureHomeNavGraph : FeatureNavGraph {

  val position: Int

  fun isNavigationBarVisible(selected: String): Boolean

  fun NavGraphBuilder.buildNavGraph(navController: NavHostController)

  @Composable
  fun RowScope.NavigationBarItem(selected: String, navController: NavHostController)
}
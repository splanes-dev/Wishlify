package com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.splanes.uoc.wishlify.presentation.feature.home.HomeRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureMainNavGraph

class HomeNavGraph : FeatureMainNavGraph {
  override fun NavGraphBuilder.buildNavGraph(navController: NavHostController) {
    composable<Home> {
      HomeRoute(navController)
    }
  }
}
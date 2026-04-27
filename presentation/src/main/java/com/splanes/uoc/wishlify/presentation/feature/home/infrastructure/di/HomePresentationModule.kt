package com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.di

import androidx.navigation.NavHostController
import com.splanes.uoc.wishlify.presentation.feature.home.HomeViewModel
import com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.navigation.HomeLauncherImpl
import com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.navigation.HomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureMainNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.HomeLauncher
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/** Koin module that wires the home presentation-layer dependencies. */
internal val HomePresentationModule = module {
  // Navigation
  singleOf(::HomeNavGraph) bind FeatureMainNavGraph::class
  factory { (navController: NavHostController) ->
    HomeLauncherImpl(navController)
  } bind HomeLauncher::class
  // ViewModel
  viewModelOf(::HomeViewModel)
}

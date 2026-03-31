package com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.di

import androidx.navigation.NavHostController
import com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.navigation.HomeLauncherImpl
import com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.navigation.HomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureMainNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.HomeLauncher
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val HomePresentationModule = module {
  singleOf(::HomeNavGraph) bind FeatureMainNavGraph::class
  factory { (navController: NavHostController) ->
    HomeLauncherImpl(navController)
  } bind HomeLauncher::class
}
package com.splanes.uoc.wishlify.presentation.feature.splash.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.splash.infrastructure.navigation.Splash
import com.splanes.uoc.wishlify.presentation.feature.splash.infrastructure.navigation.SplashNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureMainNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.MainNavStartRoute
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module that wires the presentation-layer dependencies for the splash feature.
 */
internal val SplashPresentationModule = module {
  // Navigation
  single(named(MainNavStartRoute)) { Splash } bind Any::class
  singleOf(::SplashNavGraph) bind FeatureMainNavGraph::class
}

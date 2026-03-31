package com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation.Wishlists
import com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation.WishlistsNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.HomeNavStartRoute
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

internal val WishlistsPresentationModule = module {
  // Navigation
  single(named(HomeNavStartRoute)) { Wishlists } bind Any::class
  singleOf(::WishlistsNavGraph) bind FeatureHomeNavGraph::class
}
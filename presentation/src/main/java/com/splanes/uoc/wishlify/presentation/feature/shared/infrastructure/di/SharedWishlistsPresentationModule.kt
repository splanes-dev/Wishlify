package com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlistsNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val SharedWishlistsPresentationModule = module {
  singleOf(::SharedWishlistsNavGraph) bind FeatureHomeNavGraph::class
}
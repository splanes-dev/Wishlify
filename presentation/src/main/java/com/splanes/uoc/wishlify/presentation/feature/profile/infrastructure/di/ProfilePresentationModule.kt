package com.splanes.uoc.wishlify.presentation.feature.profile.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.profile.infrastructure.navigation.ProfileNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val ProfilePresentationModule = module {
  singleOf(::ProfileNavGraph) bind FeatureHomeNavGraph::class
}
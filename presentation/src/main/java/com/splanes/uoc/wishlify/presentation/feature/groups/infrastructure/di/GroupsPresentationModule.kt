package com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.navigation.GroupsNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val GroupsPresentationModule = module {
  singleOf(::GroupsNavGraph) bind FeatureHomeNavGraph::class
}
package com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation.SecretSantaNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val SecretSantaPresentationModule = module {
  singleOf(::SecretSantaNavGraph) bind FeatureHomeNavGraph::class
}
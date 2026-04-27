package com.splanes.uoc.wishlify.data.feature.session.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.session.datasource.SessionDataSource
import com.splanes.uoc.wishlify.data.feature.session.repository.SessionRepositoryImpl
import com.splanes.uoc.wishlify.domain.feature.session.repository.SessionRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/** Koin module that wires the session data-layer components. */

internal val SessionDataModule = module {
  // Data source
  singleOf(::SessionDataSource)
  // Repository
  singleOf(::SessionRepositoryImpl) bind SessionRepository::class
}

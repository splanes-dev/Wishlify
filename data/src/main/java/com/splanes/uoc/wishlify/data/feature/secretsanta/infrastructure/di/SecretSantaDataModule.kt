package com.splanes.uoc.wishlify.data.feature.secretsanta.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.secretsanta.datasource.SecretSantaRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.secretsanta.mapper.SecretSantaDataMapper
import com.splanes.uoc.wishlify.data.feature.secretsanta.repository.SecretSantaRepositoryImpl
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/** Koin module that wires the Secret Santa data-layer components. */

internal val SecretSantaDataModule = module {
  // Data sources
  singleOf(::SecretSantaRemoteDataSource)
  // Repository
  singleOf(::SecretSantaRepositoryImpl) bind SecretSantaRepository::class
  // Mappers
  singleOf(::SecretSantaDataMapper)
}

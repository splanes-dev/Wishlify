package com.splanes.uoc.wishlify.data.feature.user.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.user.datasource.UserLocalDataSource
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.data.feature.user.repository.UserRepositoryImpl
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/** Koin module that wires the user data-layer components. */

internal val UserDataModule = module {
  // Data source
  singleOf(::UserRemoteDataSource)
  singleOf(::UserLocalDataSource)
  // Repository
  singleOf(::UserRepositoryImpl) bind UserRepository::class
  // Mapper
  singleOf(::UserDataMapper)
}

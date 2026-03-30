package com.splanes.uoc.wishlify.data.feature.user.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.data.feature.user.repository.UserRepositoryImpl
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val UserDataModule = module {
  // Data source
  singleOf(::UserRemoteDataSource)
  // Repository
  singleOf(::UserRepositoryImpl) bind UserRepository::class
  // Mapper
  singleOf(::UserDataMapper)
}
package com.splanes.uoc.wishlify.data.feature.authentication.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.authentication.datasource.AuthLocalDataSource
import com.splanes.uoc.wishlify.data.feature.authentication.datasource.AuthRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.authentication.mapper.AuthDataMapper
import com.splanes.uoc.wishlify.data.feature.authentication.repository.AuthenticationRepositoryImpl
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val AuthenticationDataModule = module {
  // DataSource
  singleOf(::AuthRemoteDataSource)
  singleOf(::AuthLocalDataSource)
  // Repository
  singleOf(::AuthenticationRepositoryImpl) bind AuthenticationRepository::class
  // Mappers
  singleOf(::AuthDataMapper)
}
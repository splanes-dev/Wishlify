package com.splanes.uoc.wishlify.data.feature.shared.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.shared.datasource.SharedWishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.shared.mapper.SharedWishlistsDataMapper
import com.splanes.uoc.wishlify.data.feature.shared.repository.SharedWishlistsRepositoryImpl
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val SharedWishlistsDataModule = module {
  // Data source
  singleOf(::SharedWishlistsRemoteDataSource)
  // Repository
  singleOf(::SharedWishlistsRepositoryImpl) bind SharedWishlistsRepository::class
  // Mappers
  singleOf(::SharedWishlistsDataMapper)
}
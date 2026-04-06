package com.splanes.uoc.wishlify.data.feature.shared.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.shared.datasource.SharedWishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.shared.mapper.SharedWishlistsDataMapper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val SharedWishlistsDataModule = module {
  // Data source
  singleOf(::SharedWishlistsRemoteDataSource)
  // Mappers
  singleOf(::SharedWishlistsDataMapper)
}
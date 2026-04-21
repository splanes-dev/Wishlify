package com.splanes.uoc.wishlify.data.feature.wishlists.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.wishlists.datasource.WishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.wishlists.mapper.WishlistsDataMapper
import com.splanes.uoc.wishlify.data.feature.wishlists.repository.WishlistsRepositoryImpl
import com.splanes.uoc.wishlify.data.feature.wishlists.util.UrlDataExtractor
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val WishlistsDataModule = module {
  // Data source
  singleOf(::WishlistsRemoteDataSource)
  // Repositories
  singleOf(::WishlistsRepositoryImpl) bind WishlistsRepository::class
  // Mappers
  singleOf(::WishlistsDataMapper)
  // Utils
  singleOf(::UrlDataExtractor)
}
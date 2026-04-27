package com.splanes.uoc.wishlify.data.common.media.infrastructure.di

import com.splanes.uoc.wishlify.data.common.media.datasource.MediaRemoteDataSource
import com.splanes.uoc.wishlify.data.common.media.mapper.ImageMediaDataMapper
import com.splanes.uoc.wishlify.data.common.media.repository.ImageMediaRepositoryImpl
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/** Koin module that wires the shared media data components. */
internal val MediaDataModule = module {
  // Data source
  singleOf(::MediaRemoteDataSource)
  // Mapper
  singleOf(::ImageMediaDataMapper)
  // Repository
  singleOf(::ImageMediaRepositoryImpl) bind ImageMediaRepository::class
}

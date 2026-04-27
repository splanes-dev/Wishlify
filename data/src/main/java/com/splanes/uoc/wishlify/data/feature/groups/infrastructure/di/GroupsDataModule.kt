package com.splanes.uoc.wishlify.data.feature.groups.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.groups.datasource.GroupsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.groups.mapper.GroupsDataMapper
import com.splanes.uoc.wishlify.data.feature.groups.repository.GroupsRepositoryImpl
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/** Koin module that wires the groups data-layer components. */
internal val GroupsDataModule = module {
  // Data source
  singleOf(::GroupsRemoteDataSource)
  // Repository
  singleOf(::GroupsRepositoryImpl) bind GroupsRepository::class
  // Mappers
  singleOf(::GroupsDataMapper)
}

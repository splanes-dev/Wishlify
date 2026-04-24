package com.splanes.uoc.wishlify.data.feature.notifications.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.notifications.datasource.NotificationsLocalDataSource
import com.splanes.uoc.wishlify.data.feature.notifications.mapper.PushNotificationDataMapper
import com.splanes.uoc.wishlify.data.feature.notifications.repository.NotificationsRepositoryImpl
import com.splanes.uoc.wishlify.domain.feature.notifications.repository.NotificationsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val PushNotificationsDataModule = module {
  // DataSources
  singleOf(::NotificationsLocalDataSource)
  // Repositories
  singleOf(::NotificationsRepositoryImpl) bind NotificationsRepository::class
  // Mappers
  singleOf(::PushNotificationDataMapper)
}
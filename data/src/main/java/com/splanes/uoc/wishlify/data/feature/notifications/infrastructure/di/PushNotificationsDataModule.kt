package com.splanes.uoc.wishlify.data.feature.notifications.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.notifications.mapper.PushNotificationDataMapper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val PushNotificationsDataModule = module {
  singleOf(::PushNotificationDataMapper)
}
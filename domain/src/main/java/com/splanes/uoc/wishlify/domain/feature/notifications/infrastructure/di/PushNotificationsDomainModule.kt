package com.splanes.uoc.wishlify.domain.feature.notifications.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.notifications.usecase.IsPermissionModalVisibleUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val PushNotificationsDomainModule = module {
  singleOf(::IsPermissionModalVisibleUseCase)
}
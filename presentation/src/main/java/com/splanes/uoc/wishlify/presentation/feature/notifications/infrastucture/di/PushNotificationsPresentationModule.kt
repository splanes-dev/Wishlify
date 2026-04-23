package com.splanes.uoc.wishlify.presentation.feature.notifications.infrastucture.di

import com.splanes.uoc.wishlify.domain.feature.notifications.PushNotificationHandler
import com.splanes.uoc.wishlify.presentation.feature.notifications.PushNotificationHandlerImpl
import com.splanes.uoc.wishlify.presentation.feature.notifications.PushNotificationPendingIntentFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val PushNotificationsPresentationModule = module {
  singleOf(::PushNotificationHandlerImpl) bind PushNotificationHandler::class
  singleOf(::PushNotificationPendingIntentFactory)
}
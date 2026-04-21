package com.splanes.uoc.wishlify.presentation.common.infrastructure.di

import com.splanes.uoc.wishlify.presentation.common.deeplink.DeeplinkMapper
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.ExternalActionHandler
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val CommonPresentationModule = module {
  singleOf(::ErrorUiMapper)
  singleOf(::DeeplinkMapper)
  singleOf(::ExternalActionHandler)
}
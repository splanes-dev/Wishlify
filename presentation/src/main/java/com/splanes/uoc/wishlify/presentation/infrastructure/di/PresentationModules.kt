package com.splanes.uoc.wishlify.presentation.infrastructure.di

import com.splanes.uoc.wishlify.presentation.common.infrastructure.di.CommonPresentationModule
import com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.di.AuthPresentationModule
import com.splanes.uoc.wishlify.presentation.feature.splash.infrastructure.di.SplashPresentationModule

val PresentationModules = listOf(
  CommonPresentationModule,
  SplashPresentationModule,
  AuthPresentationModule
)
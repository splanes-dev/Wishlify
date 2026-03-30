package com.splanes.uoc.wishlify.domain.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.authentication.infrastructure.di.AuthenticationDomainModule

val DomainModules = listOf(
  AuthenticationDomainModule,
)
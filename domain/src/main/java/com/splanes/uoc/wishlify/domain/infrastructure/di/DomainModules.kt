package com.splanes.uoc.wishlify.domain.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.authentication.infrastructure.di.AuthenticationDomainModule
import com.splanes.uoc.wishlify.domain.feature.session.infrastructure.di.SessionDomainModule
import com.splanes.uoc.wishlify.domain.feature.user.infrastructure.di.UserDomainModule
import com.splanes.uoc.wishlify.domain.feature.wishlists.infrastructure.di.WishlistsDomainModule

val DomainModules = listOf(
  AuthenticationDomainModule,
  UserDomainModule,
  SessionDomainModule,
  WishlistsDomainModule,
)
package com.splanes.uoc.wishlify.domain.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.authentication.infrastructure.di.AuthenticationDomainModule
import com.splanes.uoc.wishlify.domain.feature.groups.infrastructure.di.GroupsDomainModule
import com.splanes.uoc.wishlify.domain.feature.notifications.infrastructure.di.PushNotificationsDomainModule
import com.splanes.uoc.wishlify.domain.feature.secretsanta.infrastructure.di.SecretSantaDomainModule
import com.splanes.uoc.wishlify.domain.feature.session.infrastructure.di.SessionDomainModule
import com.splanes.uoc.wishlify.domain.feature.shared.infrastructure.di.SharedWishlistsDomainModule
import com.splanes.uoc.wishlify.domain.feature.user.infrastructure.di.UserDomainModule
import com.splanes.uoc.wishlify.domain.feature.wishlists.infrastructure.di.WishlistsDomainModule

val DomainModules = listOf(
  AuthenticationDomainModule,
  UserDomainModule,
  SessionDomainModule,
  WishlistsDomainModule,
  SharedWishlistsDomainModule,
  SecretSantaDomainModule,
  GroupsDomainModule,
  PushNotificationsDomainModule,
)
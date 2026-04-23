package com.splanes.uoc.wishlify.presentation.infrastructure.di

import com.splanes.uoc.wishlify.presentation.common.infrastructure.di.CommonPresentationModule
import com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.di.AuthPresentationModule
import com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.di.GroupsPresentationModule
import com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.di.HomePresentationModule
import com.splanes.uoc.wishlify.presentation.feature.notifications.infrastucture.di.PushNotificationsPresentationModule
import com.splanes.uoc.wishlify.presentation.feature.profile.infrastructure.di.ProfilePresentationModule
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.di.SecretSantaPresentationModule
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.di.SharedWishlistsPresentationModule
import com.splanes.uoc.wishlify.presentation.feature.splash.infrastructure.di.SplashPresentationModule
import com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.di.WishlistsPresentationModule

val PresentationModules = listOf(
  CommonPresentationModule,
  SplashPresentationModule,
  AuthPresentationModule,
  HomePresentationModule,
  WishlistsPresentationModule,
  SharedWishlistsPresentationModule,
  SecretSantaPresentationModule,
  GroupsPresentationModule,
  ProfilePresentationModule,
  PushNotificationsPresentationModule,
)
package com.splanes.uoc.wishlify.data.infrastructure.di

import com.splanes.uoc.wishlify.data.common.firebase.infrastructure.di.FirebaseModule
import com.splanes.uoc.wishlify.data.common.media.infrastructure.di.MediaDataModule
import com.splanes.uoc.wishlify.data.feature.authentication.infrastructure.di.AuthenticationDataModule
import com.splanes.uoc.wishlify.data.feature.session.infrastructure.di.SessionDataModule
import com.splanes.uoc.wishlify.data.feature.user.infrastructure.di.UserDataModule
import com.splanes.uoc.wishlify.data.feature.wishlists.infrastructure.di.WishlistsDataModule

val DataModules = listOf(
  FirebaseModule,
  MediaDataModule,
  AuthenticationDataModule,
  UserDataModule,
  SessionDataModule,
  WishlistsDataModule,
)
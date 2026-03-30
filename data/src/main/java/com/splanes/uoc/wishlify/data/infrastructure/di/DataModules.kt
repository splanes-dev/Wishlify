package com.splanes.uoc.wishlify.data.infrastructure.di

import com.splanes.uoc.wishlify.data.common.firebase.infrastructure.di.FirebaseModule
import com.splanes.uoc.wishlify.data.feature.authentication.infrastructure.di.AuthenticationDataModule
import com.splanes.uoc.wishlify.data.feature.user.infrastructure.di.UserDataModule

val DataModules = listOf(
  FirebaseModule,
  AuthenticationDataModule,
  UserDataModule,
)
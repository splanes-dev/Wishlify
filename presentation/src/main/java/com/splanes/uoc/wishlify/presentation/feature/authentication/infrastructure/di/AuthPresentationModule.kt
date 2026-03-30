package com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.di

import androidx.navigation.NavHostController
import com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.navigation.AuthLauncherImpl
import com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.navigation.AuthNavGraph
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.SignInViewModel
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.SignUpViewModel
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.mapper.SignUpFormErrorMapper
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureMainNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.AuthLauncher
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val AuthPresentationModule = module {
  // Navigation
  factory { (navController: NavHostController) ->
    AuthLauncherImpl(navController)
  } bind AuthLauncher::class

  single { AuthNavGraph() } bind FeatureMainNavGraph::class

  // SignIn
  viewModelOf(::SignInViewModel)

  // SignUp
  factoryOf(::SignUpFormErrorMapper)
  viewModelOf(::SignUpViewModel)
}
package com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.di

import androidx.navigation.NavHostController
import com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.navigation.AuthLauncherImpl
import com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.navigation.AuthNavGraph
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.SignInViewModel
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.mapper.SignInErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.mapper.SignInFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.SignUpViewModel
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.mapper.SignUpErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.mapper.SignUpFormErrorMapper
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureMainNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.AuthLauncher
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/** Koin module that wires the authentication presentation-layer dependencies. */
val AuthPresentationModule = module {
  // Navigation
  factory { (navController: NavHostController) ->
    AuthLauncherImpl(navController)
  } bind AuthLauncher::class

  single { AuthNavGraph() } bind FeatureMainNavGraph::class

  // SignIn
  factoryOf(::SignInErrorMapper)
  factoryOf(::SignInFormErrorMapper)
  viewModelOf(::SignInViewModel)

  // SignUp
  factoryOf(::SignUpErrorMapper)
  factoryOf(::SignUpFormErrorMapper)
  viewModelOf(::SignUpViewModel)
}

package com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.SignInRoute
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.SignUpRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureMainNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.Transitions
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.HomeLauncher
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

class AuthNavGraph : FeatureMainNavGraph {

  override fun NavGraphBuilder.buildNavGraph(navController: NavHostController) {
    navigation<Auth>(startDestination = SignIn) {
      composable<SignIn> {
        val homeLauncher = koinInject<HomeLauncher> { parametersOf(navController) }
        SignInRoute(
          viewModel = koinViewModel(),
          onNavToSignUp = { navController.navigate(route = SignUp) },
          onNavToHome = { homeLauncher.launch(popUpTo = SignIn) }
        )
      }

      composable<SignUp>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit
      ) {
        val homeLauncher = koinInject<HomeLauncher> { parametersOf(navController) }
        SignUpRoute(
          viewModel = koinViewModel(),
          onNavToSignIn = { navController.popBackStack() },
          onNavToHome = { homeLauncher.launch(popUpTo = SignIn) }
        )
      }
    }
  }
}
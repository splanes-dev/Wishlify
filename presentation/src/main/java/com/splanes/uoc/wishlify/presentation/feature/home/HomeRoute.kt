package com.splanes.uoc.wishlify.presentation.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NoAccounts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.home.infrastructure.navigation.Home
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation.SecretSanta
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation.SecretSantaExternalAction
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation.SecretSantaExternalActionHandler
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlistExternalAction
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlistExternalActionHandler
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlists
import com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation.WishlistExternalAction
import com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation.WishlistExternalActionHandler
import com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation.Wishlists
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.HomeNavStartRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.CreateNewWishlistItem
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.ExternalActionHandler
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.OpenDeeplink
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.launcher.AuthLauncher
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.currentKoinScope
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

@Composable
fun HomeRoute(mainNavController: NavHostController) {

  val viewModel = koinViewModel<HomeViewModel>()
  val startDestination = koinInject<Any>(named(HomeNavStartRoute))
  val authLauncher = koinInject<AuthLauncher> { parametersOf(mainNavController) }
  val navController = rememberNavController()
  val navGraphs = currentKoinScope().getAll<FeatureHomeNavGraph>()
  val externalActionHandler = koinInject<ExternalActionHandler>()
  val wishlistExternalActionHandler = koinInject<WishlistExternalActionHandler>()
  val sharedWishlistExternalActionHandler = koinInject<SharedWishlistExternalActionHandler>()
  val secretSantaExternalActionHandler = koinInject<SecretSantaExternalActionHandler>()
  val current by navController.currentBackStackEntryAsState()
  var isSignedOut by remember { mutableStateOf(false) }

  LaunchedEffect(navController) {
    externalActionHandler.consume { action ->
      when (action) {
        is CreateNewWishlistItem -> viewModel.onCreateWishlistItemByShare(action)
        is OpenDeeplink -> viewModel.onOpenDeeplink(action.deeplink)
      }
      externalActionHandler.clear()
    }
  }

  LifecycleStartEffect(Unit) {
    viewModel.observeSessionState()

    onStopOrDispose { viewModel.cancelSessionStateObserver() }
  }

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        HomeUiSideEffect.NoSession -> isSignedOut = true
        is HomeUiSideEffect.NavToSecretSanta -> {
          val action = SecretSantaExternalAction.JoinToParticipantsByToken(effect.deeplink.token)
          secretSantaExternalActionHandler.dispatch(action)
          navController.navigate(SecretSanta.List) {
            launchSingleTop = true
          }
        }

        is HomeUiSideEffect.NavToSharedWishlist -> {
          val action = SharedWishlistExternalAction.JoinToParticipantsByToken(effect.deeplink.token)
          sharedWishlistExternalActionHandler.dispatch(action)
          navController.navigate(SharedWishlists.List) {
            launchSingleTop = true
          }
        }

        is HomeUiSideEffect.NavToWishlist -> {
          val action = WishlistExternalAction.JoinToEditorByToken(effect.deeplink.token)
          wishlistExternalActionHandler.dispatch(action)
          navController.navigate(Wishlists.List) {
            launchSingleTop = true
          }
        }

        is HomeUiSideEffect.NavToWishlistNewItemByUrl -> {
          val action = WishlistExternalAction.NewItemByUrl(effect.url)
          wishlistExternalActionHandler.dispatch(action)
          navController.navigate(Wishlists.List) {
            launchSingleTop = true
          }
        }

        is HomeUiSideEffect.NavToWishlistNewItemByUri -> {
          val action = WishlistExternalAction.NewItemByUri(effect.uri)
          wishlistExternalActionHandler.dispatch(action)
          navController.navigate(Wishlists.List) {
            launchSingleTop = true
          }
        }
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
  ) {
    NavHost(
      modifier = Modifier.fillMaxSize(),
      navController = navController,
      startDestination = startDestination
    ) {
      navGraphs.forEach { navGraph ->
        navGraph.run { buildNavGraph(navController, authLauncher::launch) }
      }
    }

    if (current.isNavigationBarVisible(navGraphs)) {
      NavigationBar(
        modifier = Modifier
          .height(72.dp)
          .fillMaxWidth()
          .align(Alignment.BottomCenter),
      ) {
        navGraphs
          .sortedBy { navGraph -> navGraph.position }
          .forEach { navGraph ->
            navGraph.run { NavigationBarItem(current?.destination, navController) }
          }
      }
    }

    if (isSignedOut) {
      ErrorDialog(
        uiModel = ErrorUiModel(
          icon = Icons.Rounded.NoAccounts,
          title = stringResource(R.string.error_dialog_title_no_session),
          description = stringResource(R.string.error_dialog_description_no_session),
          dismissButton = stringResource(R.string.error_dialog_dismiss_button_default),
        ),
        onDismiss = {
          isSignedOut = false
          authLauncher.launch(popUpTo = Home)
        }
      )
    }
  }
}

private fun NavBackStackEntry?.isNavigationBarVisible(graphs: List<FeatureHomeNavGraph>): Boolean =
  graphs.any { graph -> graph.isNavigationBarVisible(this?.destination) }
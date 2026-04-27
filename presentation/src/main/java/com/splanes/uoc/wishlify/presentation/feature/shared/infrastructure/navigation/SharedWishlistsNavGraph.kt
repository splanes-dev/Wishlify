package com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Diversity3
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.chat.SharedWishlistThirdPartyChatRoute
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.SharedWishlistThirdPartyDetailRoute
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.SharedWishlistsListRoute
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.SharedWishlistsListViewModel
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.Transitions
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

/**
 * Home navigation graph that hosts the shared wishlists flows.
 */
class SharedWishlistsNavGraph : FeatureHomeNavGraph {
  /**
   * Position of the shared wishlists tab in the home navigation bar.
   */
  override val position: Int = 1

  /**
   * Keeps the bottom navigation visible only on the shared wishlists list.
   */
  override fun isNavigationBarVisible(destination: NavDestination?): Boolean =
    destination?.hasRoute(SharedWishlists.List::class) == true

  /**
   * Renders the shared wishlists item in the home navigation bar.
   */
  @Composable
  override fun RowScope.NavigationBarItem(
    current: NavDestination?,
    navController: NavHostController
  ) {
    NavigationBarItem(
      selected = current?.hasRoute(SharedWishlists.List::class) == true,
      onClick = {
        navController.navigate(SharedWishlists) {
          launchSingleTop = true
          popUpTo(navController.graph.id) {
            inclusive = true
          }
        }
      },
      icon = {
        Icon(
          imageVector = Icons.Rounded.Diversity3,
          contentDescription = stringResource(R.string.tab_wishlists_shared),
        )
      },
      label = {
        Text(
          text = stringResource(R.string.tab_wishlists_shared),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      },
      alwaysShowLabel = false,
    )
  }

  /**
   * Registers the shared wishlists feature graph, including third-party detail and chat flows.
   */
  override fun NavGraphBuilder.buildNavGraph(
    navController: NavHostController,
    onLogout: (NavOptionsBuilder.() -> Unit) -> Unit,
  ) {
    navigation<SharedWishlists>(startDestination = SharedWishlists.List) {
      composable<SharedWishlists.List> {

        val viewModel = koinViewModel<SharedWishlistsListViewModel>()
        val externalActionHandler = koinInject<SharedWishlistExternalActionHandler>()

        SharedWishlistsListRoute(
          viewModel = viewModel,
          externalActionHandler = externalActionHandler,
          onNavToThirdPartySharedWishlistDetail = { wishlist ->
            val route = SharedWishlists.ThirdPartyDetail(
              sharedWishlistId = wishlist.id,
              sharedWishlistName = wishlist.linkedWishlist.name,
              target = wishlist.linkedWishlist.target.orEmpty()
            )
            navController.navigate(route)
          }
        )
      }

      composable<SharedWishlists.ThirdPartyDetail>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit
      ) { backStackEntry ->

        val route = backStackEntry.toRoute<SharedWishlists.ThirdPartyDetail>()
        val externalActionHandler = koinInject<SharedWishlistExternalActionHandler>()

        SharedWishlistThirdPartyDetailRoute(
          viewModel = koinViewModel {
            parametersOf(
              route.sharedWishlistId,
              route.sharedWishlistName,
              route.target
            )
          },
          externalActionHandler = externalActionHandler,
          onNavToChat = {
            val route = SharedWishlists.ThirdPartyChat(
              sharedWishlistId = route.sharedWishlistId,
              sharedWishlistName = route.sharedWishlistName,
              target = route.target
            )
            navController.navigate(route)
          },
          onBack = { navController.popBackStack() }
        )
      }

      composable<SharedWishlists.ThirdPartyChat>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit,
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<SharedWishlists.ThirdPartyChat>()

        SharedWishlistThirdPartyChatRoute(
          viewModel = koinViewModel {
            parametersOf(
              route.sharedWishlistId,
              route.sharedWishlistName,
              route.target
            )
          },
          onBack = { navController.popBackStack() }
        )
      }
    }
  }
}

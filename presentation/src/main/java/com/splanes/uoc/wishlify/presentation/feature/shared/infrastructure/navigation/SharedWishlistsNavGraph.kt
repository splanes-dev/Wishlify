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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.own.SharedWishlistOwnDetailRoute
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.SharedWishlistThirdPartyDetailRoute
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.SharedWishlistsListRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class SharedWishlistsNavGraph : FeatureHomeNavGraph {
  override val position: Int = 1

  override fun isNavigationBarVisible(selected: String): Boolean =
    selected == SharedWishlists.List::class.qualifiedName

  @Composable
  override fun RowScope.NavigationBarItem(selected: String, navController: NavHostController) {
    NavigationBarItem(
      selected = selected == SharedWishlists.List::class.qualifiedName,
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

  override fun NavGraphBuilder.buildNavGraph(
    navController: NavHostController,
    onLogout: (NavOptionsBuilder.() -> Unit) -> Unit,
  ) {
    navigation<SharedWishlists>(startDestination = SharedWishlists.List) {
      composable<SharedWishlists.List> {
        SharedWishlistsListRoute(
          viewModel = koinViewModel(),
          onNavToOwnSharedWishlistDetail = { wishlist ->
            val route = SharedWishlists.OwnDetail(
              sharedWishlistId = wishlist.id,
              sharedWishlistName = wishlist.linkedWishlist.name,
              target = wishlist.linkedWishlist.target.orEmpty()
            )
            navController.navigate(route)
          },
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

      composable<SharedWishlists.ThirdPartyDetail> { backStackEntry ->

        val route = backStackEntry.toRoute<SharedWishlists.ThirdPartyDetail>()

        SharedWishlistThirdPartyDetailRoute(
          viewModel = koinViewModel {
            parametersOf(
              route.sharedWishlistId,
              route.sharedWishlistName,
              route.target
            )
          },
          onNavToChat = {},
          onBack = { navController.popBackStack() }
        )
      }

      composable<SharedWishlists.OwnDetail> { backStackEntry ->

        val route = backStackEntry.toRoute<SharedWishlists.OwnDetail>()

        SharedWishlistOwnDetailRoute(
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
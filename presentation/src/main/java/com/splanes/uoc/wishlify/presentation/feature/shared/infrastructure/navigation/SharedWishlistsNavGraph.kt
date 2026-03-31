package com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Diversity3
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph

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
      label = { Text(text = stringResource(R.string.tab_wishlists_shared)) },
      alwaysShowLabel = false,
    )
  }

  override fun NavGraphBuilder.buildNavGraph(
    navController: NavHostController,
    onLogout: (NavOptionsBuilder.() -> Unit) -> Unit,
  ) {
    navigation<SharedWishlists>(startDestination = SharedWishlists.List) {
      composable<SharedWishlists.List> {

      }
    }
  }
}
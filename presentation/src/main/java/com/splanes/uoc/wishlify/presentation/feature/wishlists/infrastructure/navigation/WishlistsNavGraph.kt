package com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph

class WishlistsNavGraph : FeatureHomeNavGraph {

  override val position: Int = 0

  override fun isNavigationBarVisible(selected: String): Boolean =
    selected == Wishlists.List::class.qualifiedName

  @Composable
  override fun RowScope.NavigationBarItem(selected: String, navController: NavHostController) {
    NavigationBarItem(
      selected = selected == Wishlists.List::class.qualifiedName,
      onClick = {
        navController.navigate(Wishlists) {
          launchSingleTop = true
          popUpTo(navController.graph.id) {
            inclusive = true
          }
        }
      },
      icon = {
        Icon(
          painter = painterResource(R.drawable.ic_wishlists),
          contentDescription = stringResource(R.string.tab_wishlists),
        )
      },
      label = { Text(text = stringResource(R.string.tab_wishlists)) },
      alwaysShowLabel = false,
    )
  }

  override fun NavGraphBuilder.buildNavGraph(
    navController: NavHostController,
    onLogout: (NavOptionsBuilder.() -> Unit) -> Unit,
  ) {
    navigation<Wishlists>(startDestination = Wishlists.List) {
      composable<Wishlists.List> {

      }
    }
  }
}
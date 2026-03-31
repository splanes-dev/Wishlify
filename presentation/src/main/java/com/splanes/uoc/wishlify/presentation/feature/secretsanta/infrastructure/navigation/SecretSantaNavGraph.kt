package com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation

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

class SecretSantaNavGraph : FeatureHomeNavGraph {

  override val position: Int = 2

  override fun isNavigationBarVisible(selected: String): Boolean =
    selected == SecretSanta.List::class.qualifiedName

  @Composable
  override fun RowScope.NavigationBarItem(selected: String, navController: NavHostController) {
    NavigationBarItem(
      selected = selected == SecretSanta.List::class.qualifiedName,
      onClick = {
        navController.navigate(SecretSanta) {
          launchSingleTop = true
        }
      },
      icon = {
        Icon(
          painter = painterResource(R.drawable.ic_secret_santa),
          contentDescription = stringResource(R.string.tab_secret_santa),
        )
      },
      label = { Text(text = stringResource(R.string.tab_secret_santa)) },
      alwaysShowLabel = false,
    )
  }

  override fun NavGraphBuilder.buildNavGraph(
    navController: NavHostController,
    onLogout: (NavOptionsBuilder.() -> Unit) -> Unit,
  ) {
    navigation<SecretSanta>(startDestination = SecretSanta.List) {
      composable<SecretSanta.List> {

      }
    }
  }
}
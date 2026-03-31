package com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
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

class GroupsNavGraph : FeatureHomeNavGraph {

  override val position: Int = 3

  override fun isNavigationBarVisible(selected: String): Boolean =
    selected == Groups.List::class.qualifiedName

  @Composable
  override fun RowScope.NavigationBarItem(selected: String, navController: NavHostController) {
    NavigationBarItem(
      selected = selected == Groups.List::class.qualifiedName,
      onClick = {
        navController.navigate(Groups) {
          launchSingleTop = true
        }
      },
      icon = {
        Icon(
          imageVector = Icons.Outlined.Groups,
          contentDescription = stringResource(R.string.tab_groups),
        )
      },
      label = { Text(text = stringResource(R.string.tab_groups)) },
      alwaysShowLabel = false,
    )
  }

  override fun NavGraphBuilder.buildNavGraph(
    navController: NavHostController,
    onLogout: (NavOptionsBuilder.() -> Unit) -> Unit,
  ) {
    navigation<Groups>(startDestination = Groups.List) {
      composable<Groups.List> {

      }
    }
  }
}
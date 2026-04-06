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
import androidx.navigation.toRoute
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.WishlistDetailRoute
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.WishlistDetailViewModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.creation.WishlistNewItemRoute
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.edition.WishlistEditItemRoute
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.WishlistShareRoute
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.WishlistsListRoute
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.WishlistsListViewModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories.WishlistsCategoriesRoute
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.WishlistsNewListRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.NavResultHandler
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.Transitions
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.popBackStackWithResult
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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
        val viewModel = koinViewModel<WishlistsListViewModel>()

        // Result handler from create wishlist
        navController.NavResultHandler<Boolean>(key = NavResult.NEW_WISHLIST) { created ->
          viewModel.onNewWishlistResult(created = created)
        }

        // Result handler from share wishlist
        navController.NavResultHandler<String>(key = NavResult.SHARE_WISHLIST) { sharedWishlistName ->
          viewModel.onWishlistShared(sharedWishlistName)
        }

        WishlistsListRoute(
          viewModel = viewModel,
          onNavToNewWishlist = { isOwn ->
            navController.navigate(Wishlists.NewList(isOwn))
          },
          onNavToWishlistDetail = { wishlist ->
            val route = Wishlists.Detail(wishlist.id, name = wishlist.title)
            navController.navigate(route)
          },
          onNavToAdminCategories = {
            navController.navigate(Wishlists.Categories)
          }
        )
      }

      composable<Wishlists.Categories>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit,
      ) {
        WishlistsCategoriesRoute(
          viewModel = koinViewModel(),
          onBack = { navController.popBackStack() }
        )
      }

      composable<Wishlists.NewList>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<Wishlists.NewList>()
        WishlistsNewListRoute(
          viewModel = koinViewModel { parametersOf(route.isOwn) },
          onFinish = { navController.popBackStackWithResult(result = it) }
        )
      }

      composable<Wishlists.Detail>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit,
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<Wishlists.Detail>()
        val viewModel = koinViewModel<WishlistDetailViewModel> {
          parametersOf(route.id, route.name)
        }

        // Result handler from create item
        navController.NavResultHandler<Boolean>(key = NavResult.NEW_ITEM) { created ->
          viewModel.onNewItemResult(created = created)
        }

        // Result handler from update item
        navController.NavResultHandler<Boolean>(key = NavResult.UPDATE_ITEM) { updated ->
          viewModel.onEditItemResult(updated = updated)
        }

        // Result handler from share-wishlist
        navController.NavResultHandler<Boolean>(key = NavResult.SHARE_WISHLIST) { shared ->
          if (shared) {
            navController.popBackStackWithResult(
              key = NavResult.SHARE_WISHLIST,
              result = route.name
            )
          }
        }

        WishlistDetailRoute(
          viewModel = viewModel,
          onNavToNewItem = { link ->
            val route = Wishlists.NewItem(wishlistId = route.id, link = link)
            navController.navigate(route)
          },
          onNavToEditItem = { itemId ->
            val route = Wishlists.EditItem(wishlistId = route.id, itemId = itemId)
            navController.navigate(route)
          },
          onNavToShare = {
            val route = Wishlists.ShareList(wishlistId = route.id)
            navController.navigate(route)
          },
          onBack = { navController.popBackStack() },
        )
      }

      composable<Wishlists.NewItem>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<Wishlists.NewItem>()
        WishlistNewItemRoute(
          viewModel = koinViewModel { parametersOf(route.wishlistId, route.link) },
          onFinish = { navController.popBackStackWithResult(key = NavResult.NEW_ITEM, result = it) }
        )
      }

      composable<Wishlists.EditItem>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<Wishlists.EditItem>()
        WishlistEditItemRoute(
          viewModel = koinViewModel { parametersOf(route.wishlistId, route.itemId) },
          onFinish = {
            navController.popBackStackWithResult(
              key = NavResult.UPDATE_ITEM,
              result = it
            )
          }
        )
      }

      composable<Wishlists.ShareList>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<Wishlists.ShareList>()
        WishlistShareRoute(
          viewModel = koinViewModel { parametersOf(route.wishlistId) },
          onNavToNewGroup = { /* TODO */ },
          onFinish = {
            navController.popBackStackWithResult(
              key = NavResult.SHARE_WISHLIST,
              result = it
            )
          }
        )
      }
    }
  }
}

private object NavResult {
  const val NEW_WISHLIST = "new-wishlist"
  const val NEW_ITEM = "new-item"
  const val UPDATE_ITEM = "update-item"
  const val SHARE_WISHLIST = "share-wishlist"
}
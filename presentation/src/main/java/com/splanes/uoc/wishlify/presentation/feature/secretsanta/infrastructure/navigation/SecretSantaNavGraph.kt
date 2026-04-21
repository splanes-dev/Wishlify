package com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.navigation.Groups
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.SecretSantaChatRoute
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.SecretSantaChatViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.SecretSantaDetailRoute
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.SecretSantaDetailViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.SecretSantaHobbiesRoute
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.SecretSantaHobbiesViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.SecretSantaListRoute
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.SecretSantaListViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.SecretSantaNewEventRoute
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.SecretSantaNewEventViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.edition.SecretSantaUpdateEventRoute
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.edition.SecretSantaUpdateEventViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.share.SecretSantaShareWishlistRoute
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.share.SecretSantaShareWishlistViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.wishlist.SecretSantaWishlistRoute
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.wishlist.SecretSantaWishlistViewModel
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.NavResultHandler
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.Transitions
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.popBackStackWithResult
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class SecretSantaNavGraph : FeatureHomeNavGraph {

  override val position: Int = 2

  override fun isNavigationBarVisible(destination: NavDestination?): Boolean =
    destination?.hasRoute(SecretSanta.List::class) == true

  @Composable
  override fun RowScope.NavigationBarItem(
    current: NavDestination?,
    navController: NavHostController
  ) {
    NavigationBarItem(
      selected = current?.hasRoute(SecretSanta.List::class) == true,
      onClick = {
        navController.navigate(SecretSanta) {
          launchSingleTop = true
          popUpTo(navController.graph.id) {
            inclusive = true
          }
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
    navigation<SecretSanta>(startDestination = SecretSanta.List(null)) {
      composable<SecretSanta.List> {
        val viewModel = koinViewModel<SecretSantaListViewModel>()

        navController.NavResultHandler<Boolean>(key = NavResult.NEW_EVENT) { created ->
          if (created) {
            viewModel.onNewEventResult()
          }
        }

        SecretSantaListRoute(
          viewModel = viewModel,
          onNavToNewEvent = { navController.navigate(SecretSanta.NewEvent) },
          onNavToDetail = { event ->
            val route = SecretSanta.Detail(eventId = event.id, name = event.name)
            navController.navigate(route)
          }
        )
      }

      composable<SecretSanta.NewEvent>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) {

        val viewModel = koinViewModel<SecretSantaNewEventViewModel>()

        navController.NavResultHandler<Boolean>(key = NavResult.NEW_GROUP) { created ->
          if (created) {
            viewModel.onNewGroupResult()
          }
        }

        navController.NavResultHandler<List<String>>(key = NavResult.SEARCH) { users ->
          if (users.isNotEmpty()) {
            viewModel.onUserSearchResult(users)
          }
        }

        SecretSantaNewEventRoute(
          viewModel = viewModel,
          onNavToCreateGroup = { navController.navigate(Groups.NewGroup) },
          onNavToSearchUsers = { navController.navigate(Groups.SearchUsers) },
          onFinish = {
            navController.popBackStackWithResult(
              key = NavResult.NEW_EVENT,
              result = it
            )
          }
        )
      }

      composable<SecretSanta.UpdateEvent>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) { backStackEntry ->

        val route = backStackEntry.toRoute<SecretSanta.UpdateEvent>()

        val viewModel = koinViewModel<SecretSantaUpdateEventViewModel> {
          parametersOf(route.eventId)
        }

        navController.NavResultHandler<Boolean>(key = NavResult.NEW_GROUP) { created ->
          if (created) {
            viewModel.onNewGroupResult()
          }
        }

        navController.NavResultHandler<List<String>>(key = NavResult.SEARCH) { users ->
          if (users.isNotEmpty()) {
            viewModel.onUserSearchResult(users)
          }
        }

        SecretSantaUpdateEventRoute(
          viewModel = viewModel,
          onNavToCreateGroup = { navController.navigate(Groups.NewGroup) },
          onNavToSearchUsers = { navController.navigate(Groups.SearchUsers) },
          onFinish = {
            navController.popBackStackWithResult(
              key = NavResult.UPDATE_EVENT,
              result = it
            )
          }
        )
      }

      composable<SecretSanta.Detail>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<SecretSanta.Detail>()

        val viewModel = koinViewModel<SecretSantaDetailViewModel> {
          parametersOf(route.eventId, route.name)
        }

        navController.NavResultHandler<Boolean>(key = NavResult.UPDATE_EVENT) { updated ->
          if (updated) {
            viewModel.onEventUpdated()
          }
        }

        navController.NavResultHandler<Boolean>(key = NavResult.WISHLIST_SHARED) { shared ->
          if (shared) {
            viewModel.onEventUpdated()
          }
        }

        navController.NavResultHandler<Boolean>(key = NavResult.WISHLIST_SHARED_UPDATED) { updated ->
          if (updated) {
            viewModel.onEventUpdated()
          }
        }

        SecretSantaDetailRoute(
          viewModel = viewModel,
          onNavToEdit = { eventId ->
            val route = SecretSanta.UpdateEvent(eventId)
            navController.navigate(route)
          },
          onNavToWishlist = { eventId, wishlistOwnerId, isOwn ->
            val route = SecretSanta.Wishlist(
              eventId = eventId,
              wishlistOwnerId = wishlistOwnerId,
              isOwnWishlist = isOwn
            )
            navController.navigate(route)
          },
          onNavToShareWishlist = { eventId ->
            val route = SecretSanta.ShareWishlist(eventId)
            navController.navigate(route)
          },
          onNavToChat = { eventId, chatType, otherUid ->
            val route = SecretSanta.AnonymousChat(
              eventId = eventId,
              type = chatType,
              otherUid = otherUid
            )
            navController.navigate(route)
          },
          onNavToHobbies = { targetUid ->
            val route = SecretSanta.Hobbies(targetUid)
            navController.navigate(route)
          },
          onNavBack = {
            navController.popBackStackWithResult(
              key = NavResult.UPDATED_FROM_DETAIL,
              result = it
            )
          }
        )
      }

      composable<SecretSanta.Wishlist>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<SecretSanta.Wishlist>()

        val viewModel = koinViewModel<SecretSantaWishlistViewModel> {
          parametersOf(
            route.eventId,
            route.wishlistOwnerId,
            route.isOwnWishlist,
          )
        }

        navController.NavResultHandler<Boolean>(key = NavResult.WISHLIST_SHARED) { shared ->
          if (shared) {
            viewModel.onWishlistChanged()
          }
        }

        SecretSantaWishlistRoute(
          viewModel = viewModel,
          onNavToShareWishlist = {
            val route = SecretSanta.ShareWishlist(route.eventId)
            navController.navigate(route)
          },
          onFinish = {
            navController.popBackStackWithResult(
              key = NavResult.WISHLIST_SHARED_UPDATED,
              result = it
            )
          }
        )
      }

      composable<SecretSanta.ShareWishlist>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<SecretSanta.ShareWishlist>()

        val viewModel = koinViewModel<SecretSantaShareWishlistViewModel> {
          parametersOf(route.eventId)
        }

        SecretSantaShareWishlistRoute(
          viewModel = viewModel,
          onFinish = {
            navController.popBackStackWithResult(
              key = NavResult.WISHLIST_SHARED,
              result = it
            )
          }
        )
      }

      composable<SecretSanta.AnonymousChat>(
        enterTransition = Transitions.SlideInHorizontal.enter,
        exitTransition = Transitions.SlideInHorizontal.exit,
      ) { backStackEntry ->
        val route = backStackEntry.toRoute<SecretSanta.AnonymousChat>()

        val viewModel = koinViewModel<SecretSantaChatViewModel> {
          parametersOf(
            route.eventId,
            route.type,
            route.otherUid
          )
        }

        SecretSantaChatRoute(
          viewModel = viewModel,
          onBack = { navController.popBackStack() }
        )
      }

      composable<SecretSanta.Hobbies>(
        enterTransition = Transitions.SlideInFromBottom.enter,
        exitTransition = Transitions.SlideInFromBottom.exit,
      ) { backStackEntry ->

        val route = backStackEntry.toRoute<SecretSanta.Hobbies>()
        val viewModel = koinViewModel<SecretSantaHobbiesViewModel> { parametersOf(route.targetUid) }
        SecretSantaHobbiesRoute(
          viewModel = viewModel,
          onCancel = { navController.popBackStack() }
        )
      }
    }
  }
}

private object NavResult {
  const val NEW_EVENT = "new-event"
  const val UPDATE_EVENT = "update-event"
  const val NEW_GROUP = "new-group"
  const val SEARCH = "search-users"
  const val UPDATED_FROM_DETAIL = "updated-from-detail"
  const val WISHLIST_SHARED = "wishlist-shared"
  const val WISHLIST_SHARED_UPDATED = "wishlist-shared-updated"
}
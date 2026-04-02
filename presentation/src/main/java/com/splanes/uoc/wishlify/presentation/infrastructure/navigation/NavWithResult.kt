package com.splanes.uoc.wishlify.presentation.infrastructure.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator

typealias NavResultCallback<T> = (T) -> Unit

// A SavedStateHandle key is used to set/get NavResultCallback<T>
private const val NavResultCallbackKey = "NavResultCallbackKey"

fun <T> NavController.setNavResultCallback(callback: NavResultCallback<T>) {
  currentBackStackEntry?.savedStateHandle?.set(NavResultCallbackKey, callback)
}

fun <T> NavController.getNavResultCallback(): NavResultCallback<T>? {
  return previousBackStackEntry?.savedStateHandle?.remove(NavResultCallbackKey)
}

fun <T> NavController.popBackStackWithResult(result: T) {
  getNavResultCallback<T>()?.invoke(result)
  popBackStack()
}

fun <R : Any, T> NavController.navigateWithResult(
  route: R,
  navResultCallback: NavResultCallback<T>,
  navOptions: NavOptions? = null,
  navigatorExtras: Navigator.Extras? = null
) {
  setNavResultCallback(navResultCallback)
  navigate(route, navOptions, navigatorExtras)
}

fun <R : Any, T> NavController.navigateWithResult(
  route: R,
  navResultCallback: NavResultCallback<T>,
  builder: NavOptionsBuilder.() -> Unit
) {
  setNavResultCallback(navResultCallback)
  navigate(route, builder)
}
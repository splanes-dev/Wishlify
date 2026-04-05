package com.splanes.uoc.wishlify.presentation.infrastructure.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import kotlinx.coroutines.flow.filterNotNull


// A SavedStateHandle key is used to set/get NavResultCallback<T>
private const val NavResultDefaultKey = "NavResultDefaultKey"
fun <T> NavController.popBackStackWithResult(key: String = NavResultDefaultKey, result: T) {
  previousBackStackEntry?.savedStateHandle?.set(key, result)
  popBackStack()
}

@Composable
fun <T> NavController.NavResultHandler(key: String = NavResultDefaultKey, action: (T) -> Unit) {
  LaunchedEffect(this) {
    val state = currentBackStackEntry?.savedStateHandle
    state
      ?.getStateFlow<T?>(key, null)
      ?.filterNotNull()
      ?.collect { result ->
        action(result)
        state[key] = null
      }
  }
}
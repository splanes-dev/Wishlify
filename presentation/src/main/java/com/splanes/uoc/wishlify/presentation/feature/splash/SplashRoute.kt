package com.splanes.uoc.wishlify.presentation.feature.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
internal fun SplashRoute(
  onNavToAuth: () -> Unit
) {

  LaunchedEffect(Unit) {
    delay(2000)
    onNavToAuth()
  }

  SplashScreen()
}
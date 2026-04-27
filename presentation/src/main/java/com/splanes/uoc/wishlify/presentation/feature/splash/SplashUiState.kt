package com.splanes.uoc.wishlify.presentation.feature.splash

/**
 * One-off effects emitted from the splash flow.
 */
sealed interface SplashUiSideEffect {
  data object NavToAuth : SplashUiSideEffect
}

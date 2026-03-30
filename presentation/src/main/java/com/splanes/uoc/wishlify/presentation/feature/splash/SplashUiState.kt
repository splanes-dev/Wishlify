package com.splanes.uoc.wishlify.presentation.feature.splash

sealed interface SplashUiSideEffect {
  data object NavToAuth : SplashUiSideEffect
}
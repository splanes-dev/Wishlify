package com.splanes.uoc.wishlify.presentation.feature.home

sealed interface HomeUiSideEffect {
  data object NoSession : HomeUiSideEffect
}
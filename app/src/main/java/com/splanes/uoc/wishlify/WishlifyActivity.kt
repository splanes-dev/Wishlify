package com.splanes.uoc.wishlify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.MainNavStartRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import org.koin.android.ext.android.inject
import org.koin.compose.currentKoinScope
import org.koin.core.qualifier.named

class WishlifyActivity : ComponentActivity() {

  private val startDestination: Any by inject(named(MainNavStartRoute))

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()

    super.onCreate(savedInstanceState)
    setContent {
      WishlifyTheme {
        WishlifyApp(
          navController = rememberNavController(),
          navGraphs = currentKoinScope().getAll(),
          startDestination = startDestination
        )
      }
    }
  }
}
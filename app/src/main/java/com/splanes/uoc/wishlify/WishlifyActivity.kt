package com.splanes.uoc.wishlify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.MainNavStartRoute
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.ExternalActionHandler
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.NewWishlistItemByImage
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.NewWishlistItemByUrl
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.OpenDeeplink
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import org.koin.android.ext.android.inject
import org.koin.compose.currentKoinScope
import org.koin.core.qualifier.named

/**
 * Main Android activity of the app.
 *
 * It installs the splash screen, resolves the initial navigation destination
 * and translates incoming Android intents into presentation-layer external
 * actions.
 */
class WishlifyActivity : ComponentActivity() {

  private val startDestination: Any by inject(named(MainNavStartRoute))
  private val externalActionsHandler: ExternalActionHandler by inject()

  /** Sets up the Compose entry point and dispatches the launch intent if needed. */
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)

    handleIntentAction(intent)?.let(externalActionsHandler::dispatch)

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

  /** Dispatches new incoming intents while the activity is already alive. */
  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleIntentAction(intent)?.let(externalActionsHandler::dispatch)
  }

  /** Maps supported Android intent payloads into app-level external actions. */
  private fun handleIntentAction(intent: Intent) = when (intent.action) {
    Intent.ACTION_SEND -> {
      when {
        intent.hasExtra(Intent.EXTRA_STREAM) ->
          (intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri)?.let(::NewWishlistItemByImage)

        intent.hasExtra(Intent.EXTRA_TEXT) ->
          intent.getStringExtra(Intent.EXTRA_TEXT)?.let(::NewWishlistItemByUrl)

        else -> null
      }
    }
    Intent.ACTION_VIEW -> {
      intent.data?.let(::OpenDeeplink)
    }
    else -> null
  }
}

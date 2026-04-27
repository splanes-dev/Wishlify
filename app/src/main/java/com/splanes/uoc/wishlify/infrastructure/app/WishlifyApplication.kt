package com.splanes.uoc.wishlify.infrastructure.app

import android.app.Application
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.splanes.uoc.wishlify.BuildConfig
import com.splanes.uoc.wishlify.data.infrastructure.di.DataModules
import com.splanes.uoc.wishlify.domain.feature.notifications.PushNotificationChannel
import com.splanes.uoc.wishlify.domain.infrastructure.di.DomainModules
import com.splanes.uoc.wishlify.presentation.infrastructure.di.PresentationModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 * Application entry point responsible for bootstrapping global app services.
 *
 * It initializes logging, Firebase App Check, notification channels and the
 * Koin dependency graph used across the app.
 */
class WishlifyApplication : Application() {

  /** Performs the global initialization required before any screen is shown. */
  override fun onCreate() {
    super.onCreate()

    // Logging
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    // Firebase App Check
    FirebaseAppCheck
      .getInstance()
      .installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())

    // Notification channels
    PushNotificationChannel.Factory.create(this)

    // DI: Koin
    startKoin {
      androidLogger()
      androidContext(this@WishlifyApplication)
      modules(PresentationModules + DomainModules + DataModules)
    }
  }
}

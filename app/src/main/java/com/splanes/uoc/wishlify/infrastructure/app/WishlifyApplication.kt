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

class WishlifyApplication : Application() {

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
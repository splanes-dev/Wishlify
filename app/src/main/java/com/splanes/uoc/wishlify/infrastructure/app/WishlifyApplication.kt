package com.splanes.uoc.wishlify.infrastructure.app

import android.app.Application
import com.splanes.uoc.wishlify.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class WishlifyApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    startKoin {
      androidLogger()
      androidContext(this@WishlifyApplication)
      modules(emptyList())
    }
  }
}
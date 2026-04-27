package com.splanes.uoc.wishlify.data.feature.user.datasource

import android.content.Context
import androidx.core.content.edit

/** Local data source that persists the device push token associated with the user. */
class UserLocalDataSource(private val context: Context) {

  private val preferences by lazy {
    context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)
  }

  /** Stores the latest device token so it can later be synced with the remote profile. */
  fun storeUserToken(token: String) {
    preferences.edit { putString(USER_TOKEN, token) }
  }

  /** Retrieves the last locally stored device token, if any. */
  fun fetchUserToken(): String? =
    preferences.getString(USER_TOKEN, null)
}

private const val USER_PREF = "wishlify.user"
private const val USER_TOKEN = "wishlify.user.device-token"

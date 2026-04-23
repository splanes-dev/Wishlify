package com.splanes.uoc.wishlify.data.feature.user.datasource

import android.content.Context
import androidx.core.content.edit

class UserLocalDataSource(private val context: Context) {

  private val preferences by lazy {
    context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)
  }

  fun storeUserToken(token: String) {
    preferences.edit { putString(USER_TOKEN, token) }
  }

  fun fetchUserToken(): String? =
    preferences.getString(USER_TOKEN, null)
}

private const val USER_PREF = "wishlify.user"
private const val USER_TOKEN = "wishlify.user.device-token"
package com.splanes.uoc.wishlify.data.feature.authentication.datasource

import android.content.Context
import com.splanes.uoc.wishlify.data.common.firebase.utils.preferences.writeJson
import com.splanes.uoc.wishlify.data.feature.authentication.model.StoredCredentials

class AuthLocalDataSource(
  private val context: Context
) {

  private val preferences by lazy {
    context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
  }

  fun storeCredentials(credentials: StoredCredentials) {
    preferences.writeJson(key = AUTH_CREDENTIALS, value = credentials)
  }
}

private const val AUTH_PREFS = "wishlify.auth"
private const val AUTH_CREDENTIALS = "wishlify.auth.credentials"
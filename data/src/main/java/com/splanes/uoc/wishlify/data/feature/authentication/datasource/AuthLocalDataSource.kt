package com.splanes.uoc.wishlify.data.feature.authentication.datasource

import android.content.Context
import androidx.core.content.edit
import com.splanes.uoc.wishlify.data.common.firebase.utils.preferences.readJson
import com.splanes.uoc.wishlify.data.common.firebase.utils.preferences.writeJson
import com.splanes.uoc.wishlify.data.feature.authentication.model.StoredCredentials

/** Local data source responsible for persisting authentication credentials on device. */
class AuthLocalDataSource(
  private val context: Context
) {

  private val preferences by lazy {
    context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
  }

  /** Stores the given serialized [credentials] in shared preferences. */
  fun storeCredentials(credentials: StoredCredentials) {
    preferences.writeJson(key = AUTH_CREDENTIALS, value = credentials)
  }

  /** Retrieves previously stored credentials, or `null` when none are available. */
  fun fetchStoredCredentials(): StoredCredentials? =
    preferences.readJson(AUTH_CREDENTIALS)

  /** Removes any persisted authentication credentials from shared preferences. */
  fun cleanStoredCredentials() {
    preferences.edit { remove(AUTH_CREDENTIALS) }
  }
}

private const val AUTH_PREFS = "wishlify.auth"
private const val AUTH_CREDENTIALS = "wishlify.auth.credentials"
